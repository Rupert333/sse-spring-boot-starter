package com.block.sse.starter.service;

import com.block.sse.starter.config.SseProperties;
import com.block.sse.starter.enums.SystemEventEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 心跳服务
 * 定期向所有连接的客户端发送心跳消息以保持连接活跃
 */
@Component
@ConditionalOnProperty(prefix = "sse", name = "heartbeat-enabled", havingValue = "true", matchIfMissing = true)
public class HeartbeatService {
    private final SseManager sseManager;
    private final SseProperties properties;
    private ScheduledExecutorService scheduler;

    private static Logger log = LoggerFactory.getLogger(HeartbeatService.class);

    public HeartbeatService(SseManager sseManager, SseProperties properties) {
        this.sseManager = sseManager;
        this.properties = properties;
    }

    @PostConstruct
    public void startHeartbeat() {
        if (!properties.isHeartbeatEnabled()) {
            log.info("Heartbeat service is disabled");
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sse-heartbeat");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(
                this::sendHeartbeat,
                properties.getHeartbeatInterval(),
                properties.getHeartbeatInterval(),
                TimeUnit.MILLISECONDS
        );

        log.info("Heartbeat service started with interval: {}ms", properties.getHeartbeatInterval());
    }

    private void sendHeartbeat() {
        Set<String> connectedClients = sseManager.getConnectedClients();
        if (connectedClients.isEmpty()) {
            return;
        }

        log.debug("Sending heartbeat to {} clients", connectedClients.size());
        connectedClients.forEach(clientId -> {
            try {
                sseManager.sendDirectMessage(clientId, null, SystemEventEnum.HEARTBEAT.name(), properties.getHeartbeatMessage());
            } catch (Exception e) {
                log.error("Failed to send heartbeat to client {}", clientId, e);
            }
        });
    }

    @PreDestroy
    public void stopHeartbeat() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("Heartbeat service stopped");
        }
    }
}

