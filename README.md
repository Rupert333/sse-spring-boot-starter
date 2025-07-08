# SSE Spring Boot Starter

一个功能完整的 Spring Boot Starter，用于简化 Server-Sent Events (SSE) 的集成和使用，支持 Redis 分布式消息传递。

## 特性

- 🚀 **开箱即用**: 零配置启动，自动装配所有必需组件
- 🔄 **Redis 支持**: 支持 Redis 发布/订阅模式，实现分布式 SSE 消息传递
- 💓 **心跳机制**: 内置心跳服务，保持连接活跃
- 📊 **连接管理**: 完整的连接生命周期管理
- 🔧 **高度可配置**: 丰富的配置选项，满足不同场景需求
- 🛡️ **异常处理**: 完善的错误处理和恢复机制

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.block.sse</groupId>
    <artifactId>sse-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置应用

在 `application.properties` 中添加配置：

```properties
# SSE 基本配置
sse.timeout=60000
sse.heartbeat-interval=15000
sse.heartbeat-enabled=true

# Redis 配置（可选，用于分布式场景）
spring.redis.host=localhost
spring.redis.port=6379
```

### 3. 使用 SSE

#### 方式一：使用默认控制器

Starter 提供了开箱即用的 REST API：

```bash
# 建立 SSE 连接
GET /sse/connect/{clientId}

# 发送消息给指定客户端
POST /sse/send/{clientId}
Content-Type: application/json
{"message": "Hello World"}

# 广播消息给所有客户端
POST /sse/broadcast
Content-Type: application/json
{"message": "Broadcast message"}

# 查看连接状态
GET /sse/status
```

#### 方式二：在代码中使用 SseManager

```java
@RestController
@RequiredArgsConstructor
public class MyController {
    
    private final SseManager sseManager;
    
    @PostMapping("/notify/{clientId}")
    public String sendNotification(@PathVariable String clientId, 
                                 @RequestBody Object message) {
        boolean sent = sseManager.sendMessage(clientId, message);
        return sent ? "发送成功" : "发送失败";
    }
}
```

### 4. 前端连接

```javascript
// 建立 SSE 连接
const eventSource = new EventSource('/sse/connect/client-001');

eventSource.onmessage = function(event) {
    const data = JSON.parse(event.data);
    console.log('收到消息:', data);
};

eventSource.onerror = function(event) {
    console.error('连接错误:', event);
};
```

## 配置选项

| 配置项                      | 默认值 | 说明 |
|--------------------------|--------|------|
| `sse.timeout`            | 30000 | SSE 连接超时时间（毫秒） |
| `sse.heartbeatInterval`  | 15000 | 心跳间隔（毫秒） |
| `sse.heartbeatEnabled`   | true | 是否启用心跳 |
| `sse.heartbeatMessage`   | "ping" | 心跳消息内容 |
| `sse.channelPrefix`      | "sse:channel:" | Redis 通道前缀 |
| `sse.reconnectDelay`     | 5000 | 客户端重连延迟（毫秒） |
| `sse.handlerType`        | "redis" | 消息处理器类型（redis/local） |
| `sse.redisEnabled`       | true | 是否启用 Redis 支持 |
| `sse.controllerEnabled`  | true | 是否启用默认控制器 |
| `sse.retrySendDelayTime` | true | 发送消息续传时时间间隔(此间隔用于控制创建连接后延迟多久发送需要续传的消息) |

## 架构设计

### 核心组件

1. **SseManager**: SSE 连接管理器，负责连接的建立、维护和消息发送
2. **MessageHandler**: 消息处理策略接口，支持 Redis 和本地两种实现
3. **SseEventObserver**: 事件观察者，监听连接生命周期事件
4. **HeartbeatService**: 心跳服务，定期发送心跳消息保持连接

### 消息流程

```
客户端请求 -> SseController -> SseManager -> MessageHandler -> Redis/本地
                    ↓
Redis 订阅消息 -> RedisSseEventObserver -> SseManager -> 客户端
```

## 使用场景

### 1. 实时通知系统

```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final SseManager sseManager;
    
    public void sendUserNotification(String userId, String message) {
        Map<String, Object> notification = Map.of(
            "type", "notification",
            "message", message,
            "timestamp", System.currentTimeMillis()
        );
        sseManager.sendMessage(userId, notification);
    }
}
```

### 2. 系统监控面板

```java
@Component
@RequiredArgsConstructor
public class SystemMonitor {
    
    private final SseManager sseManager;
    
    @Scheduled(fixedRate = 5000)
    public void broadcastSystemStatus() {
        Map<String, Object> status = Map.of(
            "cpu", getCpuUsage(),
            "memory", getMemoryUsage(),
            "timestamp", System.currentTimeMillis()
        );
        
        // 广播给所有连接的客户端
        sseManager.getConnectedClients().forEach(clientId -> 
            sseManager.sendMessage(clientId, status)
        );
    }
}
```

### 3. 聊天应用

```java
@RestController
@RequiredArgsConstructor
public class ChatController {
    
    private final SseManager sseManager;
    
    @PostMapping("/chat/send")
    public String sendMessage(@RequestBody ChatMessage message) {
        // 发送给特定用户
        sseManager.sendMessage(message.getToUserId(), message);
        return "消息已发送";
    }
}
```

## 分布式部署

在分布式环境中，多个应用实例可以通过 Redis 共享 SSE 消息：

```properties
# 应用实例 A
sse.handler-type=redis
spring.redis.host=redis-cluster-host

# 应用实例 B  
sse.handler-type=redis
spring.redis.host=redis-cluster-host
```

这样，任何一个实例发送的消息都能被其他实例的客户端接收到。

## 自定义扩展

### 自定义消息处理器

```java
@Component
public class CustomMessageHandler implements MessageHandler {
    
    @Override
    public void handleMessage(String clientId, Object message) {
        // 自定义消息处理逻辑
    }
    
    @Override
    public String getType() {
        return "custom";
    }
}
```

### 自定义事件观察者

```java
@Component
public class CustomEventObserver implements SseEventObserver {
    
    @Override
    public void onConnect(String clientId) {
        // 连接建立时的自定义逻辑
    }
    
    @Override
    public void onDisconnect(String clientId) {
        // 连接断开时的自定义逻辑
    }
    
    @Override
    public void onError(String clientId, Exception e) {
        // 错误处理逻辑
    }
}
```

## 故障排除

### 常见问题

1. **连接建立失败**
   - 检查客户端 ID 是否有效
   - 确认服务端口是否正确
   - 查看服务器日志中的错误信息

2. **消息发送失败**
   - 检查 Redis 连接状态
   - 确认客户端连接是否还活跃
   - 查看 `sse.handler-type` 配置

3. **心跳不工作**
   - 确认 `sse.heartbeat-enabled=true`
   - 检查 `sse.heartbeat-interval` 配置
   - 查看 HeartbeatService 的日志

### 调试技巧

启用调试日志：

```properties
logging.level.com.block.sse.starter=DEBUG
```

监控连接状态：

```bash
curl http://localhost:8080/sse/status
```

## 性能优化

1. **连接数限制**: 根据服务器资源调整 `sse.timeout`
2. **心跳频率**: 平衡连接保活和资源消耗，调整 `sse.heartbeat-interval`
3. **Redis 优化**: 使用 Redis 集群提高可用性和性能
