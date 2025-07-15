package com.block.sse.starter.service.impl;


import com.block.sse.starter.service.ClientService;

import java.util.HashSet;
import java.util.Set;

public class DefaultClientService implements ClientService {

    private final Set<String> clients = new HashSet<>();

    @Override
    public Boolean addClient(String clientId) {
        return clients.add(clientId);
    }

    @Override
    public Boolean removeClient(String clientId) {
        return clients.remove(clientId);
    }

    @Override
    public Set<String> getConnectedClients() {
        return clients;
    }
}
