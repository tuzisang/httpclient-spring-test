package com.electricity.httpclientspringtest.utils;

import com.electricity.httpclientspringtest.constant.HttpHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author jianwei
 * @create 2023-01-13-16:34
 */
@Slf4j
@Component
public class HttpClientUtil {

    private final CloseableHttpClient httpClient;

    public HttpClientUtil(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * 构建 使用 json的请求的 post请求
     * @param url
     * @param json
     * @param header 请求头
     * @return
     */
    private HttpUriRequest buildPostRequestWithJson(String url, String json, Header header) {
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        return  RequestBuilder.post(url)
                .setEntity(entity)
                .addHeader(header)
                .addHeader(HttpHeader.CONTENT_TYPE, HttpHeader.JSON)
                .build();

    }

    /**
     * 执行 post请求，用 json做请求体，可以设置请求头
     *
     * @param url    请求地址
     * @param json
     * @param header 请求头，设置某些验证条件
     * @return 响应结果
     */
    public String doPostWithJson(String url, String json, Header header) {
        HttpUriRequest request = buildPostRequestWithJson(url, json, header);
        return execute(request);
    }

    /**
     * 执行 post请求，用 json做请求体
     *
     * @param url  请求地址
     * @param json
     * @return 响应结果
     */
    public String doPostWithJson(String url, String json) {
        HttpUriRequest request = buildPostRequestWithJson(url, json, null);
        return execute(request);
    }

    public String doGet(String url) {
        HttpUriRequest request = RequestBuilder.get(url).build();
        return execute(request);
    }


    /**
     * 执行请求获取响应结果后关闭
     *
     * @param request 需要执行的请求
     * @return 响应的结果
     */
    private String execute(HttpUriRequest request) {
        String result = null;
        String uri = null;
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = null;
            uri = request.getURI().toString();
            if (response != null) {
                entity = response.getEntity();
            }
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            log.error("网络异常：【{}】", uri, e);
        } catch (ParseException e) {
            log.error("请求通信【{}】时解析异常，{}", uri, e);
        }
        log.info("【{}】的响应结果是：{}", uri, result);
        return result;
    }
}
