package com.block.sse.starter.observer;

/**
 * SSE事件观察者接口
 * 用于监听和处理SSE连接的生命周期事件
 */
public interface SseEventObserver {
    /**
     * 处理客户端连接事件
     *
     * @param clientId 客户端ID
     */
    void onConnect(String clientId);

    /**
     * 处理客户端断开连接事件
     *
     * @param clientId 客户端ID
     */
    void onDisconnect(String clientId);

    /**
     * 处理连接错误事件
     *
     * @param clientId 客户端ID
     * @param e 异常信息
     */
    void onError(String clientId, Exception e);
}

