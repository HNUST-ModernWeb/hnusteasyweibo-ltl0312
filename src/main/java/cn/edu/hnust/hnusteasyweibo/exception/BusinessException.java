package cn.edu.hnust.hnusteasyweibo.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑中的错误情况，如参数校验失败、状态错误等
 *
 * <p>主要特点：
 * <ul>
 *   <li>继承自RuntimeException，可在业务代码中抛出</li>
 *   <li>包含错误码（code）用于区分不同类型的业务错误</li>
 *   <li>配合GlobalExceptionHandler统一处理并返回友好的错误信息</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>用户注册时用户名已存在</li>
 *   <li>用户登录时用户名或密码错误</li>
 *   <li>查询不存在的资源</li>
 *   <li>业务状态校验失败</li>
 * </ul>
 * </p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 * @see GlobalExceptionHandler
 */
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     * 用于区分不同类型的业务错误，如400-参数错误，401-认证失败，404-资源不存在等
     */
    private int code;

    /**
     * 构造业务异常
     *
     * <p>功能说明：创建一个包含错误码和错误消息的业务异常</p>
     *
     * @param code 错误码，表示异常的类型
     * @param message 错误消息，描述具体的错误原因
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码
     *
     * <p>功能说明：返回异常的HTTP状态码或业务错误码</p>
     *
     * @return 错误码
     */
    public int getCode() {
        return code;
    }
}
