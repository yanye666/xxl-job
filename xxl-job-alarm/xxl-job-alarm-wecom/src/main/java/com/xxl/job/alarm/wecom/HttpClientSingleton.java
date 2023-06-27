package com.xxl.job.alarm.wecom;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public enum HttpClientSingleton {
    INSTANCE;

    private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }
}