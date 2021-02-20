package com.mright.discuss.framework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * HttpUtil
 *
 * @author smallk
 * @date 2018/7/29 22:15
 */
@Slf4j
public class HttpUtil {

    private static final int READ_TIMEOUT = 60000;

    private static final int CONNECT_TIMEOUT = 60000;

    /**
     * http get request
     */
    public static String get(String urlAddr, Map<String, Object> paramsMap, int connectTimeout, int readTimeout) throws Exception {
        log.info("get request url: {}, params: {}", urlAddr, JSONObject.toJSONString(paramsMap));
        String line;
        String params = "";
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        StringBuffer result = new StringBuffer();
        try {
            if (connectTimeout < 1) {
                connectTimeout = CONNECT_TIMEOUT;
            }
            if (readTimeout < 1) {
                readTimeout = READ_TIMEOUT;
            }
            if (paramsMap != null && !paramsMap.isEmpty()) {
                StringBuffer str = new StringBuffer();
                Set set = paramsMap.keySet();
                Iterator iter = set.iterator();
                while (iter.hasNext()) {
                    String key = iter.next().toString();
                    if (paramsMap.get(key) == null) {
                        continue;
                    }
                    str.append(key).append("=").append(paramsMap.get(key)).append("&");
                }
                if (str.length() > 0) {
                    params = "?" + str.substring(0, str.length() - 1);
                }
            }
            URL url = new URL(urlAddr + params);
            conn = (HttpURLConnection) url.openConnection();
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            conn.connect();
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * http post request
     */
    public static String post(String urlAddr, Map<String, Object> paramsMap, int connectTimeout, int readTimeout) throws Exception {
        log.info("post request url: {}, params: {}", urlAddr, JSONObject.toJSONString(paramsMap));
        String line;
        String params = "";
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        StringBuffer result = new StringBuffer();
        try {
            if (connectTimeout < 1) {
                connectTimeout = CONNECT_TIMEOUT;
            }
            if (readTimeout < 1) {
                readTimeout = READ_TIMEOUT;
            }
            if (paramsMap != null && !paramsMap.isEmpty()) {
                StringBuffer str = new StringBuffer();
                Set set = paramsMap.keySet();
                Iterator iter = set.iterator();
                while (iter.hasNext()) {
                    String key = iter.next().toString();
                    if (paramsMap.get(key) == null) {
                        continue;
                    }
                    str.append(key).append("=").append(paramsMap.get(key)).append("&");
                }
                if (str.length() > 0) {
                    params = str.substring(0, str.length() - 1);
                }
            }
            URL url = new URL(urlAddr);
            conn = (HttpURLConnection) url.openConnection();
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            // 设置是否向HttpURLConnection输出，因为这个是post请求，参数要放在http正文内，
            // 因此需要设为true, 默认情况下是false
            conn.setDoOutput(true);
            // 不使用缓存,默认情况下是true
            conn.setUseCaches(false);
            // 设定请求的方法为"POST",默认是GET
            conn.setRequestMethod("POST");
            if (!params.isEmpty()) {
                // 此处getOutputStream会隐含的进行connect()
                outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                // 写入
                outputStreamWriter.write(params);
                // 刷新该流的缓冲
                outputStreamWriter.flush();
            }
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * https get request
     */
    public static String getSSL(String urlAddr, Map<String, Object> paramsMap, int connectTimeout, int readTimeout) throws Exception {
        log.info("get request url: {}, params: {}", urlAddr, JSONObject.toJSONString(paramsMap));
        String line;
        String params = "";
        HttpsURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        StringBuffer result = new StringBuffer();
        TrustManager[] trustManager = {new DefaultX509TrustManager()};
        // 创建SSLContext
        SSLContext sslContext = SSLContext.getInstance("SSL");
        // 初始化
        sslContext.init(null, trustManager, new java.security.SecureRandom());
        // 获取SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        try {
            if (connectTimeout < 1) {
                connectTimeout = CONNECT_TIMEOUT;
            }
            if (readTimeout < 1) {
                readTimeout = READ_TIMEOUT;
            }
            if (paramsMap != null && !paramsMap.isEmpty()) {
                StringBuffer str = new StringBuffer();
                Set set = paramsMap.keySet();
                Iterator iter = set.iterator();
                while (iter.hasNext()) {
                    String key = iter.next().toString();
                    if (paramsMap.get(key) == null) {
                        continue;
                    }
                    str.append(key).append("=").append(paramsMap.get(key)).append("&");
                }
                if (str.length() > 0) {
                    params = "?" + str.substring(0, str.length() - 1);
                }
            }
            URL url = new URL(urlAddr + params);
            conn = (HttpsURLConnection) url.openConnection();
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            // 设置当前实例使用的SSLSocketFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * https post request
     */
    public static String postSSL(String urlAddr, Map<String, Object> paramsMap, int connectTimeout, int readTimeout) throws Exception {
        log.info("post request url: {}, params: {}", urlAddr, JSONObject.toJSONString(paramsMap));
        String line;
        String params = "";
        HttpsURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        StringBuffer result = new StringBuffer();
        TrustManager[] trustManager = {new DefaultX509TrustManager()};
        // 创建SSLContext
        SSLContext sslContext = SSLContext.getInstance("SSL");
        // 初始化
        sslContext.init(null, trustManager, new java.security.SecureRandom());
        // 获取SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        try {
            if (connectTimeout < 1) {
                connectTimeout = CONNECT_TIMEOUT;
            }
            if (readTimeout < 1) {
                readTimeout = READ_TIMEOUT;
            }
            if (paramsMap != null && !paramsMap.isEmpty()) {
                StringBuffer str = new StringBuffer();
                Set set = paramsMap.keySet();
                Iterator iter = set.iterator();
                while (iter.hasNext()) {
                    String key = iter.next().toString();
                    if (paramsMap.get(key) == null) {
                        continue;
                    }
                    str.append(key).append("=").append(paramsMap.get(key)).append("&");
                }
                if (str.length() > 0) {
                    params = str.substring(0, str.length() - 1);
                }
            }
            URL url = new URL(urlAddr);
            conn = (HttpsURLConnection) url.openConnection();
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            // 设置当前实例使用的SSLSocketFactory
            conn.setSSLSocketFactory(ssf);
            // 设置是否向HttpURLConnection输出，因为这个是post请求，参数要放在http正文内，
            // 因此需要设为true, 默认情况下是false
            conn.setDoOutput(true);
            // 不使用缓存,默认情况下是true
            conn.setUseCaches(false);
            // 设定请求的方法为"POST",默认是GET
            conn.setRequestMethod("POST");
            if (!params.isEmpty()) {
                // 此处getOutputStream会隐含的进行connect()
                outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                // 写入
                outputStreamWriter.write(params);
                // 刷新该流的缓冲
                outputStreamWriter.flush();
            }
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * http post request
     */
    public static String post(String urlAddr, String paramsStr, int connectTimeout, int readTimeout) throws Exception {
        log.info("post request url: {}, params: {}", urlAddr, paramsStr);
        String line;
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        StringBuffer result = new StringBuffer();
        try {
            if (connectTimeout < 1) {
                connectTimeout = CONNECT_TIMEOUT;
            }
            if (readTimeout < 1) {
                readTimeout = READ_TIMEOUT;
            }
            URL url = new URL(urlAddr);
            conn = (HttpURLConnection) url.openConnection();
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            // 设置是否向HttpURLConnection输出，因为这个是post请求，参数要放在http正文内，
            // 因此需要设为true, 默认情况下是false
            conn.setDoOutput(true);
            // 不使用缓存,默认情况下是true
            conn.setUseCaches(false);
            // 设定请求的方法为"POST",默认是GET
            conn.setRequestMethod("POST");
            if (paramsStr != null && !paramsStr.isEmpty()) {
                // 此处getOutputStream会隐含的进行connect()
                outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                // 写入
                outputStreamWriter.write(paramsStr);
                // 刷新该流的缓冲
                outputStreamWriter.flush();
            }
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * https post request
     */
    public static String postSSL(String urlAddr, String paramsStr, int connectTimeout, int readTimeout) throws Exception {
        log.info("post request url: {}, params: {}", urlAddr, paramsStr);
        String line;
        HttpsURLConnection conn = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        StringBuffer result = new StringBuffer();
        TrustManager[] trustManager = {new DefaultX509TrustManager()};
        // 创建SSLContext
        SSLContext sslContext = SSLContext.getInstance("SSL");
        // 初始化
        sslContext.init(null, trustManager, new java.security.SecureRandom());
        // 获取SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        try {
            if (connectTimeout < 1) {
                connectTimeout = CONNECT_TIMEOUT;
            }
            if (readTimeout < 1) {
                readTimeout = READ_TIMEOUT;
            }
            URL url = new URL(urlAddr);
            conn = (HttpsURLConnection) url.openConnection();
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            // 设置当前实例使用的SSLSocketFactory
            conn.setSSLSocketFactory(ssf);
            // 设置是否向HttpURLConnection输出，因为这个是post请求，参数要放在http正文内，
            // 因此需要设为true, 默认情况下是false
            conn.setDoOutput(true);
            // 不使用缓存,默认情况下是true
            conn.setUseCaches(false);
            // 设定请求的方法为"POST",默认是GET
            conn.setRequestMethod("POST");
            if (paramsStr != null && !paramsStr.isEmpty()) {
                // 此处getOutputStream会隐含的进行connect()
                outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                // 写入
                outputStreamWriter.write(paramsStr);
                // 刷新该流的缓冲
                outputStreamWriter.flush();
            }
            inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * ajax response
     */
    public static void toJson(JSONObject res, HttpServletResponse response) {
        PrintWriter writer = null;
        try {
            response.setContentType("application/json;charset=utf-8");
            writer = response.getWriter();
            writer.print(JSON.toJSONString(res));
        } catch (Exception e) {
            log.error("", e);
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取客户端ip地址
     */
    public static String getClientIp(HttpServletRequest request) {
        // 网宿cdn的真实ip
        String ip = request.getHeader("Cdn-Src-Ip");
        if (StringUtils.isBlank(ip) || " unknown".equalsIgnoreCase(ip)) {
            // 蓝讯cdn的真实ip
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || " unknown".equalsIgnoreCase(ip)) {
            // 获取代理ip
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || " unknown".equalsIgnoreCase(ip)) {
            // 获取代理ip
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            // 获取代理ip
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            // 获取真实ip
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 根据IP获取地址
     */
    public static JSONObject getIpAddress(String ip) {
        if (ip == null || ip.trim().length() < 1) {
            return null;
        }
        String code = "0";
        String url = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
        try {
            String res = HttpUtil.get(url, null, 0, 0);
            JSONObject json = JSONObject.parseObject(res);
            String backCode = json.getString("code");
            if (code.equals(backCode)) {
                return json.getJSONObject("data");
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 设置下载响应
     */
    public static void setDownLoadResponse(HttpServletRequest request, HttpServletResponse response, String fileName) throws Exception {
        String msie = "msie";
        String chrome = "chrome";
        String windows = "windows";
        String firefox = "firefox";
        String browserType = request.getHeader("User-Agent").toLowerCase();
        if (browserType.contains(firefox) || browserType.contains(chrome)) {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        } else if (browserType.contains(msie) || browserType.contains(windows)) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes());
        }
        // 重置
        response.reset();
        // 告知浏览器不缓存
        response.setHeader("pragma", "no-cache");
        response.setHeader("cache-control", "no-cache");
        response.setHeader("expires", "0");
        // 响应编码
        response.setCharacterEncoding("UTF-8");
        // 用给定的名称和值添加一个响应头
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
    }

    /**
     * 根据流获取字符串
     */
    public static String getInputStream(HttpServletRequest request) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        ServletInputStream servletInputStream = null;
        StringBuffer result = new StringBuffer();
        try {
            String line;
            servletInputStream = request.getInputStream();
            inputStreamReader = new InputStreamReader(servletInputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error("", e);
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (servletInputStream != null) {
                try {
                    servletInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        int i = 0;
        while (true) {
            try {
                String result = HttpUtil.get("http://www.woshipm.com/pd/4343957.html", null, 10000, 10000);
                System.out.println(i++);
                Thread.sleep(60000);
            } catch (Exception e) {
                System.out.println("异常：" + e.getMessage());
                continue;
            }
        }
    }
}
