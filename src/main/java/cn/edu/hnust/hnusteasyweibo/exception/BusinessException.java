package cn.edu.hnust.hnusteasyweibo.exception;

/**
 * 业务异常类
 * 包含错误码（code），配合GlobalExceptionHandler统一处理并返回友好的错误信息
 */
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     * 用于区分不同类型的业务错误，如400-参数错误，401-认证失败，404-资源不存在等
     */
    private int code;

    /**
     * 构造包含错误码和错误消息的业务异常
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码
     */
    public int getCode() {
        return code;
    }
}
