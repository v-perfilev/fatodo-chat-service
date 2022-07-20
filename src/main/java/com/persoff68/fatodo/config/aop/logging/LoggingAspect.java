package com.persoff68.fatodo.config.aop.logging;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.exception.AbstractException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    private static final String CONTROLLER_POINTCUT = "within(" + AppConstants.CONTROLLER_PATH + "..*)";
    private static final String TASK_POINTCUT = "within(" + AppConstants.TASK_PATH + "..*)";

    @Pointcut(CONTROLLER_POINTCUT + " || " + TASK_POINTCUT)
    public void applicationPackagePointcut() {
        // pointcut for controllers and services
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        String name = joinPoint.getSignature().getName();
        String message = e.getMessage() != null ? e.getMessage() : "NULL";

        if (e instanceof AbstractException) {
            log.warn("Exception in {}.{}() with cause = {}", declaringTypeName, name, message);
        } else {
            log.error("Exception in {}.{}() with cause = {}", declaringTypeName, name, message);
        }
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        String name = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.debug("Enter: {}.{}() with argument[s] = {}", declaringTypeName, name, args);
        try {
            Object result = joinPoint.proceed();
            log.debug("Exit: {}.{}() with result = {}", declaringTypeName, name, result);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", args, declaringTypeName, name);
            throw e;
        }
    }

}
