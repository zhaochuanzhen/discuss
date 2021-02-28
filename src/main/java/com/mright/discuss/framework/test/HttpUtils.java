package com.mright.discuss.framework.test;

import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * @author: mright
 * @date: Created in 2021/2/21 4:39 下午
 * @desc:
 */
public class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
    public static final String URL_UTF8 = "UTF-8";
    public static final String URL_GBK = "GBK";
    public static final String URL_ISO88591 = "ISO8859-1";
    private static final String SEPARATER_FLAG = "&";
    private static final String EMPTY = "";
    private static MultiThreadedHttpConnectionManager connectionManager = null;
    private static int connectionTimeOut = 3000;
    private static int socketTimeOut = 5000;
    private static int maxConnectionPerHost = 500;
    private static int maxTotalConnections = 500;
    private static HttpClient client;

    public HttpUtils() {
    }

    public static String sendPost(String url, Object obj) {
        return sendPost(url, obj, "UTF-8");
    }

    public static String sendPost(String url, Object obj, String enc) {
        String response = "";
        PostMethod postMethod = null;

        try {
            postMethod = new PostMethod(url);
            postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);
            postMethod.addRequestHeader("connection", "Keep-Alive");
            postMethod.getParams().setParameter("http.protocol.content-charset", enc);
            Class objClass = obj.getClass();
            Field[] fields = objClass.getDeclaredFields();
            NameValuePair[] names = new NameValuePair[fields.length];
            int index = 0;
            Field[] var9 = fields;
            int var10 = fields.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                Field field = var9[var11];
                field.setAccessible(true);
                String fieldName = field.getName();
                names[index++] = new NameValuePair(fieldName, field.get(obj).toString());
            }

            postMethod.setRequestBody(names);
            int statusCode = client.executeMethod(postMethod);
            if (statusCode != 200) {
                log.error("http请求发生异常,响应状态码={}, url:{},请求参数:{}", new Object[]{postMethod.getStatusCode(), url, JSON.toJSONString(obj)});
                throw new RuntimeException("响应状态码 = " + postMethod.getStatusCode());
            }

            response = postMethod.getResponseBodyAsString();
        } catch (Throwable var17) {
            log.error("http请求发生异常, url:{},请求参数:{}", new Object[]{url, JSON.toJSONString(obj), var17});
            throw new RuntimeException(var17);
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
                postMethod = null;
            }

        }

        return response;
    }

    static {
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
        connectionManager.getParams().setSoTimeout(socketTimeOut);
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
        connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
        client = new HttpClient(connectionManager);
    }
}
