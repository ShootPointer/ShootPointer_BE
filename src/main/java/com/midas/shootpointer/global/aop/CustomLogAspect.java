package com.midas.shootpointer.global.aop;

import com.midas.shootpointer.global.annotation.CustomLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Aspect
@Component
@Slf4j
public class CustomLogAspect {
    @Around("@annotation(customLog)")
    public Object logMethod(ProceedingJoinPoint joinPoint, CustomLog customLog) throws Throwable {
        String methodName=joinPoint.getSignature().getName();
        String className=joinPoint.getTarget().getClass().getSimpleName();
        String nowTime=String.valueOf(LocalTime.now());
        Object[] args=joinPoint.getArgs();

        try {
            log.info("{} : {} method : {} TIME : {}",className,args.length>0 ? args[0] : "Null",methodName,nowTime);
            return joinPoint.proceed();
        }catch (Exception e){
            log.info("ERROR {} : {} method : {} TIME : {}",className,args.length>0 ? args[0] : "Null",methodName,nowTime);
            throw e;
        }
    }
}
