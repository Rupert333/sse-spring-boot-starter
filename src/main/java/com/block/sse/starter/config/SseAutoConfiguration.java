package com.block.sse.starter.config;

import com.block.sse.starter.observer.RedisSseEventObserver;
import com.block.sse.starter.observer.SseEventObserver;
import com.block.sse.starter.service.HeartbeatService;
import com.block.sse.starter.service.SseManager;
import com.block.sse.starter.strategy.MessageHandler;
import com.block.sse.starter.strategy.RedisMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

/**
 * SSE自动配置类
 * 根据条件自动配置SSE相关的Bean
 */
@Configuration
@EnableConfigurationProperties(SseProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ComponentScan(basePackages = "com.block.sse")
@Import({RedisConfig.class})
public class SseAutoConfiguration {

    private static Logger log = LoggerFactory.getLogger(SseAutoConfiguration.class);

    /**
     * 配置SSE管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SseManager sseManager(MessageHandler messageHandler, SseProperties properties) {
        log.info("Configuring SSE Manager with handler type: {}",
                messageHandler != null ? messageHandler.getType() : "none");
        return new SseManager(messageHandler, properties);
    }

    /**
     * 配置Redis消息处理器（当Redis可用时）
     */
    @Bean
    @ConditionalOnClass({RedisTemplate.class})
    @ConditionalOnProperty(prefix = "sse", name = "handler-type", havingValue = "redis", matchIfMissing = true)
    @ConditionalOnMissingBean(MessageHandler.class)
    public RedisMessageHandler redisMessageHandler(RedisTemplate<String, String> redisTemplate,
                                                   SseProperties properties) {
        log.info("Configuring Redis Message Handler");
        return new RedisMessageHandler(redisTemplate, properties);
    }

    /**
     * 配置Redis事件观察者（当Redis可用时）
     */
    @Bean
    @ConditionalOnClass({RedisMessageListenerContainer.class})
    @ConditionalOnProperty(prefix = "sse", name = "redis-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "redisSseEventObserver")
    public RedisSseEventObserver redisSseEventObserver(RedisMessageListenerContainer listenerContainer,
                                                       SseProperties properties,
                                                       SseManager sseManager) {
        log.info("Configuring Redis SSE Event Observer");
        return new RedisSseEventObserver(listenerContainer, properties, sseManager);
    }

    /**
     * 配置心跳服务
     */
    @Bean
    @ConditionalOnProperty(prefix = "sse", name = "heartbeat-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public HeartbeatService heartbeatService(SseManager sseManager, SseProperties properties) {
        log.info("Configuring Heartbeat Service with interval: {}ms", properties.getHeartbeatInterval());
        return new HeartbeatService(sseManager, properties);
    }

    /**
     * 注册事件观察者到SSE管理器
     */
    @Bean
    @ConditionalOnMissingBean(name = "sseObserverRegistrar")
    public SseObserverRegistrar sseObserverRegistrar(SseManager sseManager,
                                                     List<SseEventObserver> observers) {
        return new SseObserverRegistrar(sseManager, observers);
    }

    /**
     * 事件观察者注册器
     * 负责将所有SseEventObserver注册到SseManager
     */
    public static class SseObserverRegistrar {
        public SseObserverRegistrar(SseManager sseManager, List<SseEventObserver> observers) {
            observers.forEach(observer -> {
                sseManager.addObserver(observer);
                log.info("Registered SSE event observer: {}", observer.getClass().getSimpleName());
            });
            log.info("Total {} SSE event observers registered", observers.size());
        }
    }
}

