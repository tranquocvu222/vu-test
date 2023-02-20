package com.nals.auction.client;

import com.nals.auction.client.InternalClientConfigFactory.InternalClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public abstract class BaseClient {
    private final RestTemplate restTemplate;
    private final InternalClientConfigFactory internalClientConfigFactory;

    public BaseClient(final RestTemplate restTemplate,
                      final InternalClientConfigFactory internalClientConfigFactory) {
        this.restTemplate = restTemplate;
        this.internalClientConfigFactory = internalClientConfigFactory;
    }

    protected abstract InternalClient getInternalClient();

    protected String getBaseUri() {
        return internalClientConfigFactory.getInternalClientConfig(getInternalClient()).getBaseUri();
    }

    protected HttpHeaders getHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        return headers;
    }

    protected <T> ResponseEntity<T> get(final String url, final Class<T> responseEntity) {
        var requestEntity = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(url, GET, requestEntity, responseEntity);
    }

    protected <T> ResponseEntity<T> post(final String url, final Object request, final Class<T> responseEntity) {
        return restTemplate.postForEntity(url, request, responseEntity);
    }

    protected <T> ResponseEntity<T> post(final String apiUrl, final Object request, final ParameterizedTypeReference<T> responseEntity) {
        var requestEntity = new HttpEntity<>(request, getHeaders());
        return restTemplate.exchange(apiUrl, POST, requestEntity, responseEntity);
    }

    protected <T> ResponseEntity<T> put(final String apiUrl, final Object request, final Class<T> responseEntity) {
        var requestEntity = new HttpEntity<>(request, getHeaders());
        return restTemplate.exchange(apiUrl, PUT, requestEntity, responseEntity);
    }
}
