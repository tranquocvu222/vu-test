package com.nals.auction.client;

import com.nals.auction.config.ApplicationProperties;
import com.nals.auction.config.ApplicationProperties.InternalClientConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InternalClientConfigFactory {
    private final Map<String, InternalClientConfig> internalClientConfig;

    public InternalClientConfigFactory(final ApplicationProperties applicationProperties) {
        this.internalClientConfig = applicationProperties.getInternalClients();

        for (InternalClient client : InternalClient.values()) {
            if (!internalClientConfig.containsKey(client.name().toLowerCase())) {
                throw new IllegalArgumentException(client.name() + " client must be configured");
            }
        }
    }

    public InternalClientConfig getInternalClientConfig(final InternalClient internalClient) {
        return internalClientConfig.get(internalClient.name().toLowerCase());
    }

    public enum InternalClient {
        UAA, MASTER
    }
}
