package com.satishnagc.myretail.config

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.ProtocolOptions
import com.datastax.driver.core.QueryOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.SocketOptions
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy
import com.datastax.driver.core.policies.FallthroughRetryPolicy
import com.datastax.driver.core.policies.LoadBalancingPolicy
import com.datastax.driver.core.policies.LoggingRetryPolicy
import com.datastax.driver.core.policies.RoundRobinPolicy
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.satishnagc.myretail.common.MyRetailConstants
import groovy.util.logging.Slf4j
import org.apache.http.impl.NoConnectionReuseStrategy
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.retry.annotation.EnableRetry
import org.springframework.web.client.RestTemplate

import javax.net.ssl.SSLContext

@Configuration
@EnableRetry
@EnableAspectJAutoProxy
@Slf4j
class MyRetailConfig {


    //TODO add PropertySource to read @Config values instead of hardcode.
    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()

        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
        objectMapper.serializationInclusion = JsonInclude.Include.NON_EMPTY
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        objectMapper
    }

    // Http Client
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


    // Cassandra Config

    Session session


    @Bean
    CassandraSessionService getSessionService() {
        new CassandraSessionService(cluster: createCluster())
    }

    @Bean
    Session cassandraSession() {

        try {
            log.debug("creatingCassandraSession")
            session = sessionService.getSession(MyRetailConstants.MYRETAIL_KEYSPACE)
        } catch (Exception e){
            log.warn("Error getting cassandra session; keyspace=${keySpace}", e)
        }
        session
    }


    Map getClusterSettings(String resourceName){
        try {
            final URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName)

            if (!url) {
                throw new FileNotFoundException("resource " + resourceName + " not found.")
            }

            final InputStream inputStream = url?.openStream()

            return objectMapper()?.readValue(inputStream, Map)
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getCause())
        } catch (final IOException e) {
            throw new RuntimeException(e)
        }
    }

    private Cluster createCluster() {


        String username = null
        String password = null
        String[] contactPoints = ['127.0.0.1']

        Cluster.Builder builder = Cluster.builder()
                .addContactPoints( contactPoints)
                .withPort(ProtocolOptions.DEFAULT_PORT)
                .withLoadBalancingPolicy(getLoadBalancingPolicy())
                .withRetryPolicy(new LoggingRetryPolicy(FallthroughRetryPolicy.INSTANCE))
                .withPoolingOptions( new PoolingOptions())
                .withSocketOptions(getSocketOptions())
                                                                        // initial delay ,   max delay
                .withReconnectionPolicy(new ExponentialReconnectionPolicy(1000l, 5000l))
                .withQueryOptions(new QueryOptions(consistencyLevel: ConsistencyLevel.LOCAL_ONE))
                .withCompression(ProtocolOptions.Compression.SNAPPY)
                .withProtocolVersion(2)
                .withAuthProvider((username)?new PlainTextAuthProvider(username, password) : null )

        // dont need JMX and metrics as of now

        builder.build()
    }

    SocketOptions getSocketOptions(){

        Integer readTimeout = 5000
        Integer connectTimeout = 100

        final SocketOptions socketOptions = new SocketOptions()

        socketOptions.setConnectTimeoutMillis(connectTimeout)
        socketOptions.setReadTimeoutMillis(readTimeout)
        socketOptions.setKeepAlive(true)

        socketOptions
    }

    LoadBalancingPolicy getLoadBalancingPolicy() {
        LoadBalancingPolicy loadBalancingPolicy = new RoundRobinPolicy()
        // no handle for DC AWARE, not token aware , not latency aware
        // no whitelist
        loadBalancingPolicy
    }
}




