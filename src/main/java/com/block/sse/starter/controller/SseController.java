package com.block.sse.starter.controller;

import com.block.sse.starter.service.SseManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
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
     * @param message 消息内容
     * @return 响应结果
     */
    @PostMapping("/send/{clientId}")
    public ResponseEntity<String> sendMessage(
            @PathVariable("clientId") String clientId,
            @RequestBody Object message) {
        boolean sent = sseManager.sendMessage(clientId, message);
        return sent
                ? ResponseEntity.ok("Message sent successfully")
                : ResponseEntity.notFound().build();
    }

    /**
     * 获取所有连接状态
     *
     * @return 连接状态映射
     */
    @GetMapping("/status")
    public Map<String, String> getStatus() {
        return sseManager.getStatus();
    }

    /**
     * 广播消息到所有连接的客户端
     *
     * @param message 消息内容
     * @return 响应结果
     */
    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcast(@RequestBody Object message) {
        int successCount = 0;
        for (String clientId : sseManager.getConnectedClients()) {
            if (sseManager.sendMessage(clientId, message)) {
                successCount++;
            }
        }
        return ResponseEntity.ok(String.format("Message sent to %d clients", successCount));
    }
}

