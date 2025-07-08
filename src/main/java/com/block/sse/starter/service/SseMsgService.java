package com.block.sse.starter.service;

import com.block.sse.starter.domain.MsgRequest;

import java.util.List;

/**
 * SSE 消息服务接口
 * 定义了 SSE 消息的生成、存储和检索逻辑，用于支持消息续传。
 * Starter 的使用者可以自定义实现此接口以集成自己的消息存储系统。
 * <p>
 * 默认方法提供了不处理 eventId 的基本行为。如果使用者需要消息续传功能，
 * 必须提供 generateMsgId() 和 storeMessage() 的自定义实现，
 * 并实现 getMissingMessages() 方法。
 */
public interface SseMsgService {

    /**
     * 生成一个唯一的 SSE 消息 ID。
     * 建议使用单调递增的 ID，以便于续传时排序。
     * <p>
     * 默认实现返回 null，表示不处理 eventId 逻辑。
     * 如果需要 eventId 功能，请覆盖此方法并返回有效的 ID。
     *
     * @return 唯一的字符串消息 ID，如果不需要 eventId 逻辑则返回 null
     */
    String generateMsgId();

    /**
     * 根据客户端 ID 和上次收到的事件 ID 获取缺失的消息列表。
     * 当客户端重新连接时，会调用此方法来获取自上次连接以来错过的消息。
     * 返回的消息列表应按 msgId 升序排列。
     * <p>
     * 此方法为抽象方法，必须由实现类提供具体逻辑，因为它是续传的核心。
     * 如果使用者不希望支持续传，可以返回空列表。
     *
     * @param clientId    客户端 ID
     * @param lastEventId 客户端上次收到的事件 ID (Last-Event-ID 请求头的值)
     * @return 缺失的消息列表，每个 Map 包含 "id" 和 "data" 键。如果不支持续传，返回空列表。
     */
    default MsgRequest getMessagesByEventId(String clientId, String lastEventId) {
        return new MsgRequest();
    }

    /**
     * 根据客户端 ID 和上次收到的事件 ID 获取缺失的消息列表。
     * 当客户端重新连接时，会调用此方法来获取自上次连接以来错过的消息。
     * 返回的消息列表应按 msgId 升序排列。
     * <p>
     * 此方法为抽象方法，必须由实现类提供具体逻辑，因为它是续传的核心。
     * 如果使用者不希望支持续传，可以返回空列表。
     *
     * @param clientId    客户端 ID
     * @param lastEventId 客户端上次收到的事件 ID (Last-Event-ID 请求头的值)
     * @return 缺失的消息列表，每个 Map 包含 "id" 和 "data" 键。如果不支持续传，返回空列表。
     */
    List<MsgRequest> getMessagesAfter(String clientId, String lastEventId);
}
