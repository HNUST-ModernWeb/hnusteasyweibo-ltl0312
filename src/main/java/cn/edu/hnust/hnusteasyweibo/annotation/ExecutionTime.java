package cn.edu.hnust.hnusteasyweibo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法执行时间注解
 * 标注在方法上，用于触发AOP切面自动记录方法执行时间
 * 配合ExecutionTimeAspect使用，自动记录方法的开始时间、结束时间和执行时长
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionTime {
    /**
     * 方法标识符，用于存储和查询方法执行时间
     */
    String value() default "";

    /**
     * 是否在方法执行完毕后自动记录操作日志
     */
    boolean logOperation() default false;

    /**
     * 操作日志的操作类型，如LOGIN、LOGOUT、CREATE等
     */
    String operationType() default "";

    /**
     * 操作日志的操作模块，如USER、POST、COMMENT等
     */
    String operationModule() default "";

    /**
     * 操作日志的操作描述
     */
    String operationDesc() default "";

    /**
     * HTTP请求方法，如GET、POST、PUT、DELETE
     */
    String requestMethod() default "POST";

    /**
     * 请求的接口地址
     */
    String requestUrl() default "";
}