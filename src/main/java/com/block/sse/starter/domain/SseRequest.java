package com.block.sse.starter.domain;

import javax.validation.constraints.NotEmpty;

/**
 * SSE 请求对象
 *
 * @author yangyg
 * @date 2025/7/2 17:39
 */
public class SseRequest {
    @NotEmpty(message = "clientId不能为空")
    private String clientId;
    @NotEmpty(message = "eventName不能为空")
    private String eventName;
    private Object data;

    public SseRequest() {
    }

    public SseRequest(String clientId, String eventName, Object data) {
        this.clientId = clientId;
        this.eventName = eventName;
        this.data = data;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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
        return "{\"clientId\":\"" + clientId + "\",\"eventName\":\"" + eventName + "\",\"data\":" + data + "}";
    }
}
