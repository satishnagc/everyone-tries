package com.satishnagc.myretail.config

import org.apache.http.impl.NoConnectionReuseStrategy
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

import javax.net.ssl.SSLContext

@Configuration
class MyRetailConfig {

    @Bean
    RestTemplate myRetailRestTemplate(){
        createSpringTemplate()
    }


    protected RestTemplate createSpringTemplate(SSLContext sslContext = null) {
        Integer connectionTimeout = 1000
        Integer readTimeout = 3000
        Integer maxConnectionsPerRoute = 5
        Integer maxConnectionsTotal = 5

        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
                .setMaxConnPerRoute(maxConnectionsPerRoute)
                .setMaxConnTotal(maxConnectionsTotal)
                .disableAutomaticRetries()
                .useSystemProperties()
                .setDefaultCookieStore(new BasicCookieStore())
                .setSSLContext(sslContext)
                .build()

        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                httpClient: httpclient, connectTimeout: connectionTimeout,
                readTimeout: readTimeout)

        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory)

        restTemplate
    }
}
