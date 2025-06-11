package com.midas.shootpointer.global.util;

import com.midas.shootpointer.global.annotation.CustomLog;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @CustomLog("== Spring Bean 주입 ==")
    public static <T> T getBean(Class<T> beanClass) {
        try {
            return context.getBean(beanClass);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SPRING_CONTEXT_BEAN_NOT_FOUND);
        }

    }
}
