package com.midas.shootpointer.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Redisson Distributed Lock annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 락 이름
     */
    String key();

    /**
     * 락 시간 - 디폴트:초 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 락 획득을 위해 기다리는 시간 (디폴트 - 5s)
     */
    long waitTime() default 5L;

    /**
     * 락 임대 시간 (디폴트 - 3s)
     */
    long leaseTime() default 3L;

}
