package com.block.sse.starter.domain;

import javax.validation.constraints.NotEmpty;

/**
 * SSE 请求对象
 *
 * @author yangyg
 * @date 2025/7/2 17:39
 */
public class MsgRequest {
    @NotEmpty(message = "clientId不能为空")
    private String clientId;
    private String eventId;
    @NotEmpty(message = "eventName不能为空")
    private String eventName;
    private Object data;

    public MsgRequest() {
    }

    public MsgRequest(String clientId, String eventId, String eventName, Object data) {
        this.clientId = clientId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.data = data;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        // json形式
        return "{\"clientId\":\"" + clientId + "\",\"eventId\":\"" + eventId + "\",\"eventName\":\"" + eventName + "\",\"data\":" + data + "}";
    }
}
