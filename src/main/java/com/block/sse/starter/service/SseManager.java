package com.block.sse.starter.service;

import com.block.sse.starter.config.SseProperties;
import com.block.sse.starter.enums.SystemEventEnum;
import com.block.sse.starter.observer.SseEventObserver;
import com.block.sse.starter.strategy.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * SSE连接管理器
 * 负责管理SSE连接的核心类，处理连接的建立、消息发送和连接关闭
 */
@Component
public class SseManager {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final List<SseEventObserver> observers = new ArrayList<>();

    private final MessageHandler messageHandler;
    private final SseProperties properties;

    private static Logger log = LoggerFactory.getLogger(SseManager.class);

    public SseManager(@Autowired(required = false) MessageHandler messageHandler, SseProperties properties) {
        this.messageHandler = messageHandler;
        this.properties = properties;
    }

    /**
     * 添加事件观察者
     *
     * @param observer 要添加的观察者
     */
    public void addObserver(SseEventObserver observer) {
        observers.add(observer);
    }

    /**
     * 建立新的SSE连接
     *
     * @param clientId 客户端ID
     * @return SSE发射器实例
     */
    public SseEmitter connect(String clientId) {
        SseEmitter emitter = new SseEmitter(properties.getTimeout());

        emitter.onCompletion(() -> {
            disconnect(clientId);
            notifyObservers(obs -> obs.onDisconnect(clientId));
        });

        emitter.onTimeout(() -> {
            log.warn("SSE connection timeout for client: {}", clientId);
            disconnect(clientId);
            notifyObservers(obs -> obs.onDisconnect(clientId));
        });

        emitter.onError((ex) -> {
            log.error("SSE connection error for client: {}", clientId, ex);
            disconnect(clientId);
            notifyObservers(obs -> obs.onError(clientId, (Exception) ex));
        });

        emitters.put(clientId, emitter);
        // 发送连接确认消息
        sendDirectMessage(clientId, SystemEventEnum.CONNECT.name(), properties.getHeartbeatMessage());
        // 通知观察者
        notifyObservers(obs -> obs.onConnect(clientId));

        return emitter;
    }

    /**
     * 发送消息到指定客户端（通过消息处理器）
     *
     * @param clientId 目标客户端ID
     * @param message  消息内容
     * @return 发送是否成功
     */
    public boolean sendMessage(String clientId, String eventName, Object message) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            log.warn("sending message directly");
            return sendDirectMessage(clientId, eventName, message);
        }
        try {
            messageHandler.handleMessage(clientId, eventName, message);
            return true;
        } catch (Exception e) {
            log.error("Error sending message to client {} via handler", clientId, e);
            notifyObservers(obs -> obs.onError(clientId, e));
            return false;
        }
    }

    /**
     * 直接发送消息到指定客户端
     *
     * @param clientId 目标客户端ID
     * @param message  消息内容
     * @return 发送是否成功
     */
    public boolean sendDirectMessage(String clientId, String eventName, Object message) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter == null) {
            log.warn("No SSE connection found for client: {}", clientId);
            return false;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(message)
                    .reconnectTime(properties.getReconnectDelay()));
            log.debug("Message sent directly to client: {}, eventName:{}", clientId, eventName);
            return true;
        } catch (IOException e) {
            log.error("Failed to send message to client: {}", clientId, e);
            disconnect(clientId);
            notifyObservers(obs -> obs.onError(clientId, e));
            return false;
        }
    }

    /**
     * 处理客户端错误
     *
     * @param clientId 客户端ID
     * @param e        异常
     */
    public void handleClientError(String clientId, Exception e) {
        log.error("Client error for {}", clientId, e);
        disconnect(clientId);
        notifyObservers(obs -> obs.onError(clientId, e));
    }

    /**
     * 获取所有连接的状态
     *
     * @return 客户端ID到连接状态的映射
     */
    public Map<String, String> getStatus() {
        Map<String, String> status = new ConcurrentHashMap<>();
        emitters.forEach((clientId, emitter) -> status.put(clientId, "connected"));
        return status;
    }

    /**
     * 获取所有已连接的客户端ID列表
     *
     * @return 客户端ID集合
     */
    public Set<String> getConnectedClients() {
        return new HashSet<>(emitters.keySet());
    }

    /**
     * 断开指定客户端的连接
     *
     * @param clientId 客户端ID
     */
    private void disconnect(String clientId) {
        SseEmitter emitter = emitters.remove(clientId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("Error completing SSE emitter for client: {}", clientId, e);
            }
        }
    }

    /**
     * 通知所有观察者
     *
     * @param action 要执行的观察者操作
     */
    private void notifyObservers(Consumer<SseEventObserver> action) {
        observers.forEach(observer -> {
            try {
                action.accept(observer);
            } catch (Exception e) {
                log.error("Error notifying observer", e);
            }
        });
    }
}

