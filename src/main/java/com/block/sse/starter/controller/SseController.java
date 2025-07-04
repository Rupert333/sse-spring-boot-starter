package com.block.sse.starter.controller;

import com.block.sse.starter.domain.SseRequest;
import com.block.sse.starter.service.SseManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * SSE控制器
 * 提供SSE连接和消息发送的REST API
 */
@RestController
@RequestMapping("/sse")
@ConditionalOnProperty(prefix = "sse", name = "controller-enabled", havingValue = "true", matchIfMissing = true)
@CrossOrigin(origins = "*")
public class SseController {

    private final SseManager sseManager;

    public SseController(SseManager sseManager) {
        this.sseManager = sseManager;
    }

    /**
     * 建立SSE连接
     *
     * @param clientId 客户端ID
     * @return SSE发射器
     */
    @GetMapping("/connect/{clientId}")
    public SseEmitter connect(@PathVariable("clientId") String clientId) {
        return sseManager.connect(clientId);
    }

    /**
     * 发送消息到指定客户端
     *
     * @param clientId 客户端ID
     * @param message  消息内容
     * @return 响应结果
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody @Validated SseRequest request) {
        boolean sent = sseManager.sendMessage(request.getClientId(), request.getEventName(), request.getData());
        return sent
                ? ResponseEntity.ok("Message sent successfully")
                : ResponseEntity.notFound().build();
    }

    /**
     * 获取当前实例所有连接状态
     *
     * @return 连接状态映射
     */
    @GetMapping("/status")
    public Map<String, String> getStatus() {
        return sseManager.getStatus();
    }
}

