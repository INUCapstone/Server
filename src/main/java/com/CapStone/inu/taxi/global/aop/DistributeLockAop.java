package com.CapStone.inu.taxi.global.aop;

import com.CapStone.inu.taxi.global.annotation.DistributeLock;
import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributeLockAop {
    private static final String REDISSON_KEY_PREFIX = "RLOCK_";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;


    // @DistributeLock이 걸린 메소드에 대해 실행
    // ProceedJoinPoint는 실제 메소드 호출을 가로채고 실행을 제어할 수 있는 객체
    @Around("@annotation(com.CapStone.inu.taxi.global.annotation.DistributeLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 메소드에 대한 정보를 가져옴
        Method method = signature.getMethod(); // 실제 메소드 객체를 가져옴
        DistributeLock distributeLock = method.getAnnotation(DistributeLock.class); // @DistributeLock의 속성값 가져옴

        String key = REDISSON_KEY_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributeLock.key());

        RLock rLock = redissonClient.getLock(key); // Reddison의 분산락 객체를 가져옴. 락의 제어권을 가짐

        try {
            // 락 획득 시도
            boolean available =
                    rLock.tryLock(distributeLock.waitTime(), distributeLock.leaseTime(), distributeLock.timeUnit());
            if (!available) {
                return false;
            }

            log.info("get lock success {}" , key);
            return aopForTransaction.proceed(joinPoint); // 독립된 트랜잭션으로 비지니스 로직 실행
        } catch (InterruptedException e) {
            throw new CustomException(StatusCode.LOCK_ACQUISITION_TOO_MANY_REQUESTS); // 락 획득 실패 시 예외처리
        } finally {
            rLock.unlock();
        }
    }
}
