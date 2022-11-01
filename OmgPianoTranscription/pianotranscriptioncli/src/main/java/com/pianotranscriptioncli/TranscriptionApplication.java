package com.pianotranscriptioncli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TranscriptionApplication {
    public static void main(String[] args) {
        // 注:这里传入的字段码对象，必需是声明了@SpringBootApplication的类
        //启动SpringBoot程序
        SpringApplication.run(TranscriptionApplication.class, args);
    }
}
