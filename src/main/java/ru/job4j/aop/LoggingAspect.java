package ru.job4j.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* ru.job4j.bmb.services.*.*(..))")
    private void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        var values = joinPoint.getArgs();
        var params = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < values.length; i++) {
            System.out.println(params[i] + ": " + values[i]);
        }
    }
}
