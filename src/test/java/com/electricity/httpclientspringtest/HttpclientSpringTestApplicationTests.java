package com.electricity.httpclientspringtest;

import com.electricity.httpclientspringtest.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
@Slf4j
class HttpclientSpringTestApplicationTests {

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Test
    void contextLoads() {
        String result = httpClientUtil.doGet("https://www.baidu.com/");
    }

}
