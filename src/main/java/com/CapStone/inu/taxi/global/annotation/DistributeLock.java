package com.CapStone.inu.taxi.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLock {
    String key(); // 락의 키(이름)
    TimeUnit timeUnit() default TimeUnit.SECONDS; // 시간 단위
    long waitTime() default 5L; // 대기 시간
    long leaseTime() default 3L; // 점유 시간
}
