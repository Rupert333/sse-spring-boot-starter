package com.block.sse.starter.strategy;

import com.block.sse.starter.config.SseProperties;
import com.block.sse.starter.domain.MsgRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis消息处理器
 * 通过Redis发布/订阅机制处理SSE消息
 */
@Component
@ConditionalOnClass(RedisTemplate.class)
public class RedisMessageHandler implements MessageHandler {
    private final RedisTemplate<String, String> redisTemplate;
    private final SseProperties properties;

    private static Logger log = LoggerFactory.getLogger(RedisMessageHandler.class);

    public RedisMessageHandler(RedisTemplate<String, String> redisTemplate, SseProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public void handleMessage(String clientId, String eventId, String eventName, Object message) {
        try {
            String channel = properties.getChannelPrefix() + clientId;
            MsgRequest request = new MsgRequest(clientId, eventId, eventName, message);
            redisTemplate.convertAndSend(channel, request.toString());
            log.debug("Message sent to Redis channel: {} for client: {}, eventName{}", channel, clientId, eventName);
        } catch (Exception e) {
            log.error("Failed to send message to Redis for client: {}", clientId, e);
            throw e;
        }
    }

    @Override
    public String getType() {
        return "redis";
    }
}

