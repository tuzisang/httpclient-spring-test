package com.electricity.httpclientspringtest.constant;

import java.nio.charset.StandardCharsets;

/**
 * @author jianwei
 * @create 2023-01-13-15:52
 */
public class HttpHeader {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String JSON = "application/json;charset=" + StandardCharsets.UTF_8.name();
    public static final String XML = "application/xml;charset=" + StandardCharsets.UTF_8.name();
    public static final String URLENCODED = "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name();
    public static final String MULTIPART = "multipart/form-data;charset=" + StandardCharsets.UTF_8.name();
    public static final String TEXT = "text/plain;charset=" + StandardCharsets.UTF_8.name();

}
