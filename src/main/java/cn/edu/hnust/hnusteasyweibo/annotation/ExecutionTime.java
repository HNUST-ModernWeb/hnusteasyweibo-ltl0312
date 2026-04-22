package cn.edu.hnust.hnusteasyweibo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法执行时间注解
 * 标注在方法上，用于触发AOP切面自动记录方法执行时间
 *
 * <p>主要功能：
 * <ul>
 *   <li>标识需要记录执行时间的方法</li>
 *   <li>为方法指定一个唯一标识符，用于后续查询执行时间</li>
 *   <li>支持自动记录操作日志到数据库</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>
 * {@code @ExecutionTime(value = "userLogin", logOperation = true, operationType = "LOGIN", operationModule = "USER", operationDesc = "用户登录")}
 *  public void logLogin(Long userId, String username, HttpServletRequest request) {
 *      // 方法体
 *  }
 * </pre>
 * </p>
 *
 * <p>配合AOP使用：
 * <ul>
 *   <li>ExecutionTimeAspect拦截带有此注解的方法</li>
 *   <li>自动记录方法的开始时间、结束时间和执行时长</li>
 *   <li>在finally块中自动记录操作日志（当logOperation=true时）</li>
 * </ul>
 * </p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 * @see cn.edu.hnust.hnusteasyweibo.aop.ExecutionTimeAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionTime {
    /**
     * 方法标识符
     *
     * <p>功能说明：指定一个唯一标识符，用于存储和查询方法的执行时间</p>
     *
     * <p>使用说明：
     * <ul>
     *   <li>应使用有意义的字符串作为标识符</li>
     *   <li>在调用ExecutionTimeService.getFormattedExecutionTime()时传入此值</li>
     *   <li>如果不指定，默认为空字符串</li>
     * </ul>
     * </p>
     *
     * @return 方法标识符
     */
    String value() default "";

    /**
     * 是否记录操作日志
     *
     * <p>功能说明：指定是否在方法执行完毕后自动记录操作日志到数据库</p>
     *
     * <p>使用说明：
     * <ul>
     *   <li>默认为false，即不自动记录日志</li>
     *   <li>设置为true时，AOP会在finally块中调用LogService记录日志</li>
     * </ul>
     * </p>
     *
     * @return 是否记录操作日志
     */
    boolean logOperation() default false;

    /**
     * 操作类型
     *
     * <p>功能说明：指定操作日志的操作类型，如LOGIN、LOGOUT、CREATE等</p>
     *
     * @return 操作类型
     */
    String operationType() default "";

    /**
     * 操作模块
     *
     * <p>功能说明：指定操作日志的操作模块，如USER、POST、COMMENT等</p>
     *
     * @return 操作模块
     */
    String operationModule() default "";

    /**
     * 操作描述
     *
     * <p>功能说明：指定操作日志的操作描述，对操作的详细文字说明</p>
     *
     * @return 操作描述
     */
    String operationDesc() default "";

    /**
     * 请求方法
     *
     * <p>功能说明：指定HTTP请求方法，如GET、POST、PUT、DELETE</p>
     *
     * @return 请求方法
     */
    String requestMethod() default "POST";

    /**
     * 请求URL
     *
     * <p>功能说明：指定请求的接口地址</p>
     *
     * @return 请求URL
     */
    String requestUrl() default "";
}