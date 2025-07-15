package com.block.sse.starter.service;

import java.util.Set;

/**
 * 用于维护已链接的client
 *
 * @author yangyg
 * @date 2025/7/15 10:31
 */
public interface ClientService {

    /**
     * 添加一个client
     *
     * @param clientId
     * @return
     */
    Boolean addClient(String clientId);

    /**
     * 移除一个client
     *
     * @param clientId
     * @return
     */
    Boolean removeClient(String clientId);

    /**
     * 获取已链接的client
     *
     * @return
     */
    Set<String> getConnectedClients();
}
