package hr.algebra.tracefood.webapp.Aspects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
    @Pointcut("execution(* hr.algebra.tracefood.webapp.controller.*.*(..))")
    public void pointCut() {}

    @Before("pointCut()")
    public void logMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();

        StringBuilder message = new StringBuilder();
        message.append("Method ").append(methodName).append(" called with arguments: ");

        for (Object arg : arguments) {
            message.append(arg).append(", ");
        }

        System.out.println(message.toString());
    }
}

