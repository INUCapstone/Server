package com.CapStone.inu.taxi.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AopForTransaction {

    // 독립적인 트랜잭션을 만들어 비지니스 로직이 롤백과 상관 없이 락 해제를 하기 위함이다.
    // 기존 트랜잭션에 합류하면 비지니스 로직이 롤백되면 락해제도 롤백되서 락해제가 제대로 실행되지않아 데드락을 일으킬 수 있다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
