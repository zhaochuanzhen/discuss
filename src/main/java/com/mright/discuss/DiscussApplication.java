package com.mright.discuss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mright.discuss.platform.dao")
public class DiscussApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscussApplication.class, args);
    }

}
