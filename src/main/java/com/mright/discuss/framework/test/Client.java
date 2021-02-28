package com.mright.discuss.framework.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author: mright
 * @date: Created in 2021/2/21 4:23 下午
 * @desc:
 */
public class Client {

    public static void main(String[] args) throws Exception {
        String path = "https://www.jianshu.com/p/2076f1db4d2e";
        Map<String, String> request = new HashMap<>();
        Random random = new Random();
//        request.put("id", random.nextInt(10) + "");
        String response = HttpUtils.sendPost(path, request);
        System.out.println(response);
    }
}
