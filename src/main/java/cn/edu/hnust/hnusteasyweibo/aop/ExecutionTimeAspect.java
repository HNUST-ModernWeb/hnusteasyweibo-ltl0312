package cn.edu.hnust.hnusteasyweibo.aop;

import cn.edu.hnust.hnusteasyweibo.annotation.ExecutionTime;
import cn.edu.hnust.hnusteasyweibo.constant.LogConstants;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    private static final ConcurrentHashMap<String, Double> executionTimes = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Pointcut("@annotation(cn.edu.hnust.hnusteasyweibo.annotation.ExecutionTime)")
    public void executionTimePointcut() {
    }

    @Around("executionTimePointcut()")
    public Object recordExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTimeNano = System.nanoTime();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ExecutionTime annotation = method.getAnnotation(ExecutionTime.class);
        String customName = annotation.value();

        String methodName = customName;
        if (methodName == null || methodName.isEmpty()) {
            methodName = joinPoint.getTarget().getClass().getName() + "." + method.getName();
        }

        String key = methodName;
        Object result = null;
        Throwable thrownException = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            thrownException = e;
            throw e;
        } finally {
            long endTimeNano = System.nanoTime();
            long executionTimeNanos = endTimeNano - startTimeNano;
            double executionTimeMillis = executionTimeNanos / 1_000_000.0;
            if (executionTimeMillis == 0 && executionTimeNanos > 0) {
                executionTimeMillis = 0.01;
            }
            executionTimeMillis = Math.round(executionTimeMillis * 100.0) / 100.0;

            Duration duration = Duration.ofNanos(executionTimeNanos);
            String formattedTime = formatDuration(duration);

            executionTimes.put(key, executionTimeMillis);

            if (thrownException != null) {
                log.warn("方法 {} 执行失败，耗时: {} ({}ms)", methodName, formattedTime, executionTimeMillis);
            } else {
                log.info("方法 {} 执行完成，耗时: {} ({}ms)", methodName, formattedTime, executionTimeMillis);
            }

            if (annotation.logOperation()) {
                try {
                    logOperation(joinPoint, annotation, executionTimeMillis, thrownException);
                } catch (Exception e) {
                    log.error("AOP记录操作日志失败: {}", e.getMessage());
                }
            }
        }
    }

    private void logOperation(ProceedingJoinPoint joinPoint, ExecutionTime annotation, double executionTimeMillis, Throwable thrownException) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        Long userId = null;
        String username = null;
        jakarta.servlet.http.HttpServletRequest request = null;

        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Object arg = args[i];
            if (arg == null) continue;

            if ("userId".equals(paramName) && arg instanceof Long) {
                userId = (Long) arg;
            } else if ("username".equals(paramName) && arg instanceof String) {
                username = (String) arg;
            } else if ("request".equals(paramName) && arg instanceof jakarta.servlet.http.HttpServletRequest) {
                request = (jakarta.servlet.http.HttpServletRequest) arg;
            }
        }

        LogService logService = applicationContext.getBean(LogService.class);
        logService.logOperation(
                userId,
                username,
                annotation.operationType(),
                annotation.operationModule(),
                annotation.operationDesc(),
                annotation.requestMethod(),
                annotation.requestUrl(),
                200,
                thrownException != null ? LogConstants.REQUEST_STATUS_ERROR : LogConstants.REQUEST_STATUS_SUCCESS,
                getRequestParams(joinPoint),
                request,
                executionTimeMillis
        );
    }

    private String getRequestParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            String paramName = paramNames[i];
            Object arg = args[i];
            if (arg == null) {
                sb.append(paramName).append("=null");
            } else if ("request".equals(paramName) || "password".equals(paramName) || "token".equals(paramName)) {
                sb.append(paramName).append("=[敏感信息]");
            } else {
                sb.append(paramName).append("=").append(arg.toString());
            }
        }
        return sb.toString();
    }

    public static double getExecutionTime(String methodName) {
        String key = methodName;
        return executionTimes.getOrDefault(key, 0.0);
    }

    public static String getFormattedExecutionTime(String methodName) {
        double millis = getExecutionTime(methodName);
        if (millis == 0) {
            return "0ms";
        }
        Duration duration = Duration.ofNanos((long) (millis * 1_000_000));
        return formatDuration(duration);
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        long millis = duration.toMillis() % 1000;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s ");
        }
        if (millis > 0 || sb.length() == 0) {
            sb.append(millis).append("ms");
        }

        return sb.toString().trim();
    }
}