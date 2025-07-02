package com.block.sse.starter.strategy;

/**
 * 消息处理策略接口
 * 定义了处理SSE消息的标准接口
 */
public interface MessageHandler {
    /**
     * 处理发送给指定客户端的消息
     *
     * @param clientId 客户端ID
     * @param message 要发送的消息内容
     */
    void handleMessage(String clientId, Object message);

    /**
     * 获取消息处理器类型
     *
     * @return 处理器类型标识符
     */
    String getType();
}

