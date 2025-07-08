package com.block.sse.starter.observer;

import com.block.sse.starter.config.SseProperties;
import com.block.sse.starter.domain.MsgRequest;
import com.block.sse.starter.service.SseManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Redis SSE事件观察者
 * 处理Redis发布/订阅消息并转发给SSE客户端
 */
@Component
@ConditionalOnClass(RedisMessageListenerContainer.class)
public class RedisSseEventObserver implements SseEventObserver {
    private final RedisMessageListenerContainer listenerContainer;
    private final SseProperties sseProperties;
    private final SseManager sseManager;

    private static Logger log = LoggerFactory.getLogger(RedisSseEventObserver.class);

    public RedisSseEventObserver(RedisMessageListenerContainer listenerContainer, SseProperties sseProperties, SseManager sseManager) {
        this.listenerContainer = listenerContainer;
        this.sseProperties = sseProperties;
        this.sseManager = sseManager;
    }

    // 存储客户端ID和对应的消息监听器
    private final ConcurrentMap<String, MessageListener> clientListeners = new ConcurrentHashMap<>();

    @Override
    public void onConnect(String clientId) {
        try {
            // 为新连接的客户端创建 Redis 订阅
            ChannelTopic topic = new ChannelTopic(sseProperties.getChannelPrefix() + clientId);

            // 创建消息监听器
            MessageListener messageListener = new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] pattern) {
                    try {
                        String messageBody = new String(message.getBody());
                        log.debug("Received Redis message for client {}: {}", clientId, messageBody);

                        // messageBody -> SSeRequest
                        ObjectMapper mapper = new ObjectMapper();
                        MsgRequest request = mapper.readValue(messageBody, MsgRequest.class);

                        // 直接发送消息给SSE客户端
                        sseManager.sendDirectMessage(clientId, request.getEventId(), request.getEventName(), request.getData());
                    } catch (Exception e) {
                        log.error("Error processing Redis message for client {}", clientId, e);
                        sseManager.handleClientError(clientId, e);
                    }
                }
            };

            // 添加监听器
            listenerContainer.addMessageListener(messageListener, topic);
            clientListeners.put(clientId, messageListener);

            log.debug("Redis subscription created for client: {}", clientId);
        } catch (Exception e) {
            log.error("Failed to create Redis subscription for client: {}", clientId, e);
        }
    }

    @Override
    public void onDisconnect(String clientId) {
        try {
            MessageListener listener = clientListeners.remove(clientId);
            if (listener != null) {
                ChannelTopic topic = new ChannelTopic(sseProperties.getChannelPrefix() + clientId);
                listenerContainer.removeMessageListener(listener, topic);
                log.debug("Redis subscription removed for client: {}", clientId);
            }
        } catch (Exception e) {
            log.error("Failed to remove Redis subscription for client: {}", clientId, e);
        }
    }

    @Override
    public void onError(String clientId, Exception e) {
        log.error("SSE error for client: {}", clientId, e);
        onDisconnect(clientId);
    }
}

