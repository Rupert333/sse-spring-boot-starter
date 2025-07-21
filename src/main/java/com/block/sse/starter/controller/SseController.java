package com.block.sse.starter.controller;

import com.block.sse.starter.domain.MsgRequest;
import com.block.sse.starter.service.ClientService;
import com.block.sse.starter.service.SseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * SSE控制器
 * 提供SSE连接和消息发送的REST API
 */
@RestController
@RequestMapping("/sse")
@ConditionalOnProperty(prefix = "sse", name = "controller-enabled", havingValue = "true", matchIfMissing = true)
@CrossOrigin(origins = "*")
public class SseController {

    @Resource
    private SseManager sseManager;
    @Resource
    private ClientService clientService;

    private static Logger log = LoggerFactory.getLogger(SseController.class);


    /**
     * 建立SSE连接
     *
     * @param clientId 客户端ID
     * @return SSE发射器
     */
    @GetMapping("/connect/{clientId}")
    public SseEmitter connect(@PathVariable("clientId") String clientId,
                              HttpServletRequest request) {
        String lastEventId = request.getHeader("Last-Event-ID");
        log.info("Client :{} connecting. Last-Event-ID:{}", clientId, lastEventId);
        return sseManager.connect(clientId, lastEventId);
    }

    /**
     * 关闭SSE连接
     *
     * @param clientId 客户端ID
     * @return SSE发射器
     */
    @GetMapping("/close/{clientId}")
    public void close(@PathVariable("clientId") String clientId) {
        log.info("Client :{} close", clientId);
        sseManager.disconnect(clientId);
    }

    /**
     * 发送消息到指定客户端
     *
     * @param clientId 客户端ID
     * @param message  消息内容
     * @return 响应结果
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody @Validated MsgRequest request) {
        boolean sent = sseManager.sendMessage(request.getClientId(), request.getEventId(), request.getEventName(), request.getData());
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
    public Set<String> getStatus() {
        return clientService.getConnectedClients();
    }
}

