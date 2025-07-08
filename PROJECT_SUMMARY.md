# SSE Spring Boot Starter 项目总结

## 项目结构

```
sse-spring-boot-starter/                   # Starter 模块
├── pom.xml                                # Starter 依赖配置
├── README.md                              # 详细文档
└── src/main/
    ├── java/com/block/sse/starter/
    │   ├── config/                       # 配置类
    │   │   ├── SseProperties.java        # 配置属性
    │   │   ├── SseAutoConfiguration.java # 自动配置
    │   │   └── RedisConfig.java          # Redis 配置
    │   ├── strategy/                     # 策略模式
    │   │   ├── MessageHandler.java       # 消息处理接口
    │   │   ├── RedisMessageHandler.java  # Redis 处理器
    │   ├── observer/                     # 观察者模式
    │   │   ├── SseEventObserver.java     # 事件观察者接口
    │   │   └── RedisSseEventObserver.java # Redis 事件观察者
    │   ├── service/                      # 核心服务
    │   │   ├── SseManager.java           # SSE 管理器
    │   │   └── HeartbeatService.java     # 心跳服务
    │   └── controller/                   # 控制器
    │       └── SseController.java        # SSE REST API
    └── resources
        ├── META-INF
        │ └── spring.factories            # 自动配置说明
        └── banner
            └── banner.txt                # banner

```

## 核心功能

### 1. 自动配置
- 基于条件的 Bean 配置
- Redis 可用性检测
- 消息处理器策略选择
- 事件观察者自动注册

### 2. 消息处理策略
- **Redis 模式**: 支持分布式部署，通过 Redis 发布/订阅传递消息


### 3. 连接管理
- 连接生命周期管理
- 自动断开检测
- 异常处理和恢复

### 4. 心跳机制
- 可配置的心跳间隔
- 保持连接活跃
- 自动清理无效连接

### 5. 事件观察
- 连接建立/断开事件
- 错误事件处理
- 可扩展的观察者模式

## 使用方式

### 快速开始
1. 添加依赖到项目
2. 配置 Redis（可选）
3. 启动应用
4. 访问 `/sse/connect/{clientId}` 建立连接
5. 使用 `/sse/send/{clientId}` 发送消息

### 扩展性
- 支持自定义消息处理器
- 支持自定义事件观察者
- 支持自定义控制器

## 技术特点

- **零配置启动**: 开箱即用的自动配置
- **高可用**: 支持 Redis 集群和分布式部署
- **高性能**: 异步消息处理和连接管理
- **易扩展**: 基于策略模式和观察者模式的设计
- **易使用**: 提供完整的 REST API 和示例代码

