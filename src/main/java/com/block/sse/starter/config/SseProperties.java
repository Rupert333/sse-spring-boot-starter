package com.block.sse.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSE配置属性类
 * 用于配置SSE（Server-Sent Events）相关的参数
 *
 * @author sse-starter
 * @since 1.0.0
 */
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

    /**
     * 是否启用默认控制器
     */
    private Boolean controllerEnabled = true;

    /**
     * 发送消息续传时时间间隔(此间隔用于控制创建连接后延迟多久发送需要续传的消息)
     */
    private Long retrySendDelayTime = 5000L;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public String getChannelPrefix() {
        return channelPrefix;
    }

    public void setChannelPrefix(String channelPrefix) {
        this.channelPrefix = channelPrefix;
    }

    public long getReconnectDelay() {
        return reconnectDelay;
    }

    public void setReconnectDelay(long reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public boolean isHeartbeatEnabled() {
        return heartbeatEnabled;
    }

    public void setHeartbeatEnabled(boolean heartbeatEnabled) {
        this.heartbeatEnabled = heartbeatEnabled;
    }

    public String getHeartbeatMessage() {
        return heartbeatMessage;
    }

    public void setHeartbeatMessage(String heartbeatMessage) {
        this.heartbeatMessage = heartbeatMessage;
    }

    public boolean isRedisEnabled() {
        return redisEnabled;
    }

    public void setRedisEnabled(boolean redisEnabled) {
        this.redisEnabled = redisEnabled;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public Long getRetrySendDelayTime() {
        return retrySendDelayTime;
    }

    public void setRetrySendDelayTime(Long retrySendDelayTime) {
        this.retrySendDelayTime = retrySendDelayTime;
    }

    public Boolean getControllerEnabled() {
        return controllerEnabled;
    }

    public void setControllerEnabled(Boolean controllerEnabled) {
        this.controllerEnabled = controllerEnabled;
    }
}

