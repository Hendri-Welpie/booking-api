package org.project.bookingapi;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(org.project.bookingapi..*)")
    public void basePackagePointcut() {
    }

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceAnnotationPointcut() {
    }

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repositoryAnnotationPointcut() {
    }

    @Pointcut("@within(org.springframework.stereotype.Component)")
    public void componentAnnotationPointcut() {
    }

    @Around("basePackagePointcut() && (serviceAnnotationPointcut() || repositoryAnnotationPointcut() || componentAnnotationPointcut())")
    public Object logAround(final ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(
                "Request for {}.{}() with arguments[s]={}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        );

        Instant start = Instant.now();
        Object returnValue = null;

        try {
            returnValue = joinPoint.proceed();

            if (returnValue != null) {
                log.info(
                        "Response for {}.{} with Result = {}",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        returnValue
                );
            } else {
                log.info(
                        "Response for {}.{} completed with EMPTY result",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName()
                );
            }

            return returnValue;
        } catch (Throwable ex) {
            log.error(
                    "Response for {}.{}() with arguments[s]={} failed with cause={}, message={}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()),
                    ex.getCause() != null ? ex.getCause() : "Unknown Exception occurred",
                    ex.getMessage() != null ? ex.getMessage() : "Unknown"
            );
            throw ex;
        } finally {
            log.info(
                    "Time Taken = {} ms",
                    Duration.between(start, Instant.now()).toMillis()
            );
        }
    }
}
