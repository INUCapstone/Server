package com.CapStone.inu.taxi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class Scheduler{

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4); // 스레드 풀 크기 설정
        scheduler.setThreadNamePrefix("task-scheduler-"); // 스레드 이름 접두사 설정, 디버깅이나 로그 추적 시 식별가능
        return scheduler;
    }
}

