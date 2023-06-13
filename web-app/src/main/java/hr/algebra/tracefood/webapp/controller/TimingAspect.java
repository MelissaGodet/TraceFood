package hr.algebra.tracefood.webapp.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class TimingAspect {
    private StopWatch time = new StopWatch();

    @Pointcut("execution(* hr.algebra.tracefood.webapp.controller.OperationController.add*(..))")
    public void pointCut() {}

    @Before("pointCut()")
    public void startTime() {
        time.start();
    }

    @AfterReturning(pointcut = "pointCut()", returning = "result")    public void stopTime(JoinPoint joinPoint, Object result) {
        time.stop();
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Duration of the method " + methodName + ": " + time.getTotalTimeMillis() + " ms");
    }
}
