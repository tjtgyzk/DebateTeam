package edu.xdu.debateteam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
//让使用@Async的方法在多线程的环境下被异步调用
@EnableAsync
public class ThreadPoolConfig {
}
