package com.mright.discuss.framework.utils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 当方法为空是默认为所有的链接都为安全,
 * 也就是所有的链接都能够访问到.
 * 当然这样有一定的安全风险,可以根据实际需要写入内容.
 *
 * @author smallk
 * @date 2018/7/29 22:15
 */
public class DefaultX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
