package com.aworld.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${aw.info.base-package}
@SpringBootApplication(scanBasePackages = {"${aw.info.base-package}"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
