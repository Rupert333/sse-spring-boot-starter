package com.block.sse.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSE配置属性类
 * 用于配置SSE（Server-Sent Events）相关的参数
 *
 * @author sse-starter
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sse")
public class SseProperties {
    /**
     * SSE连接超时时间（毫秒）
     * 当客户端连接超过此时间没有活动时，连接将被自动关闭
     * 默认值：30000（30秒）
     */
    private long timeout = 30000;

    /**
     * 心跳消息发送间隔（毫秒）
     * 用于保持连接活跃的心跳包发送频率
     * 默认值：15000（15秒）
     */
    private long heartbeatInterval = 15000;

    /**
     * Redis消息通道前缀
     * 用于区分不同客户端的消息通道
     * 默认值："sse:channel:"
     */
    private String channelPrefix = "sse:channel:";

    /**
     * 客户端重连延迟时间（毫秒）
     * 客户端断开连接后重新连接的等待时间
     * 默认值：5000（5秒）
     */
    private long reconnectDelay = 5000;

    /**
     * 最大重试次数
     * 发送消息失败时的最大重试次数
     * 默认值：3
     */
    private int maxRetryAttempts = 3;

    /**
     * 是否启用心跳机制
     * 控制是否发送定期心跳消息以保持连接
     * 默认值：true
     */
    private boolean heartbeatEnabled = true;

    /**
     * 心跳消息内容
     * 定期发送的心跳消息内容
     * 默认值："ping"
     */
    private String heartbeatMessage = "ping";

    /**
     * 是否启用Redis支持
     * 默认值：true
     */
    private boolean redisEnabled = true;

    /**
     * 消息处理器类型
     * 默认值："redis"
     */
    private String handlerType = "redis";
}

