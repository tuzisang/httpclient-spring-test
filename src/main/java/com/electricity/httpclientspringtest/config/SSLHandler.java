package com.electricity.httpclientspringtest.config;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 绕过证书
 * @author jianwei
 * @create 2023-01-13-14:40
 */
public class SSLHandler implements X509TrustManager, HostnameVerifier {
    @Override
    public boolean verify(String param, SSLSession session) {
        return true;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }
}
