package com.electricity.httpclientspringtest.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 配置 httpClient 的 连接池、重试机制、请求超时时长、绕过 https证书等
 * @author jianwei
 * @create 2023-01-13-14:26
 */
@ConfigurationProperties(prefix = "httpclient")
@Configuration
@Slf4j
@Data
public class HttpClientConfig {

    private int connectMax;
    private int connectRoute;
    private int socketTimeOut;
    private int requestTimeOut;
    private int connectTimeOut;
    private int retryTime;
    private String sslVersion;


    @Bean
    public SSLHandler sslHandler(){
        return new SSLHandler();
    }

    @Bean
    public SSLContext sslContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance(sslVersion);
            sslContext.init(null, new TrustManager[]{sslHandler()}, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("获取SSLContext异常：{}", e);
        }
        return sslContext;
    }

    @Bean
    public Registry<ConnectionSocketFactory> registry() {
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", new SSLConnectionSocketFactory(sslContext(), sslHandler()))
                .build();
    }

    /**
     * 连接池管理的参数配置
     * @return 连接池管理者
     */
    @Bean
    public PoolingHttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry());
        connectionManager.setMaxTotal(connectMax);
        connectionManager.setDefaultMaxPerRoute(connectRoute);
        connectionManager.setValidateAfterInactivity(0);
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(socketTimeOut)
                .build();
        connectionManager.setDefaultSocketConfig(socketConfig);
        return connectionManager;
    }

    /**
     * 重试处理器的参数配置
     * @return 重试处理器
     */
    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler() {
        return (exception, executionCount, context) -> {
            if (executionCount >= retryTime) {
                log.error("重试次数：{}", executionCount);
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                log.error("服务器掉了连接 -->重试", exception.getCause());
                return true;
            }
            // SSL握手异常 --> 不重试
            if (exception instanceof SSLHandshakeException) {
                log.error("SSL握手异常：{}", exception.getCause());
                return false;
            }
            // 超时 --> 重试
            if (exception instanceof InterruptedIOException) {
                log.error("超时,准备重试：{}", exception);
                return true;
            }
            // 目标服务器不可达 --> 不重试
            if (exception instanceof UnknownHostException) {
                log.error("目标服务器不可达：{}", exception.getCause());
                return false;
            }
            // 连接被拒绝 --> 不重试
            if (exception instanceof HttpHostConnectException) {
                log.error("连接被拒绝：{}", exception.getCause());
                return false;
            }
            // SSL握手异常 --> 不重试
            if (exception instanceof SSLException) {
                log.error("SSL握手异常：{}", exception.getCause());
                return false;
            }
            HttpClientContext hcc = HttpClientContext.adapt(context);
            HttpRequest request = hcc.getRequest();
            // 请求是幂等 --> 重试
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                log.error("幂等请求,准备重试：{}", exception.getCause());
                return true;
            }
            // 其他异常不重试
            return false;
        };
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(requestTimeOut)
                .setConnectTimeout(connectTimeOut)
                .setSocketTimeout(socketTimeOut)
                .build();
        return HttpClients.custom()
                .setConnectionManager(connectionManager())
                .setRetryHandler(httpRequestRetryHandler())
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
