package cn.edu.hnust.hnusteasyweibo.exception;

import cn.edu.hnust.hnusteasyweibo.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理应用程序中发生的各种异常，并返回格式统一的错误响应
 *
 * <p>主要功能：
 * <ul>
 *   <li>捕获并处理BusinessException业务异常</li>
 *   <li>捕获并处理其他通用Exception系统异常</li>
 *   <li>将异常信息格式化为统一的ResponseDTO错误响应</li>
 * </ul>
 * </p>
 *
 * <p>工作流程：
 * <ol>
 *   <li>Controller抛出异常</li>
 *   <li>异常被@RestControllerAdvice拦截</li>
 *   <li>根据异常类型匹配对应的@ExceptionHandler方法</li>
 *   <li>记录异常日志并返回统一的错误响应</li>
 * </ol>
 * </p>
 *
 * <p>使用说明：
 * <ul>
 *   <li>@RestControllerAdvice注解标识为全局异常处理组件</li>
 *   <li>异常处理方法返回ResponseDTO格式的错误信息</li>
 *   <li>业务异常返回具体的错误码和消息</li>
 *   <li>系统异常统一返回500状态码</li>
 * </ul>
 * </p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 * @see BusinessException
 * @see ResponseDTO
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * <p>功能说明：捕获并处理业务逻辑中抛出的BusinessException</p>
     *
     * <p>处理流程：
     * <ol>
     *   <li>记录错误日志</li>
     *   <li>将异常转换为统一的错误响应格式</li>
     *   <li>返回包含错误码和错误消息的ResponseDTO</li>
     * </ol>
     * </p>
     *
     * <p>响应示例：
     * <pre>
     * {
     *     "code": 400,
     *     "message": "用户名已存在",
     *     "data": null
     * }
     * </pre>
     * </p>
     *
     * @param e 业务异常对象，包含错误码和错误消息
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseDTO<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResponseDTO.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理系统异常
     *
     * <p>功能说明：捕获并处理未预期的系统异常</p>
     *
     * <p>处理流程：
     * <ol>
     *   <li>记录完整错误日志（包括异常堆栈）</li>
     *   <li>返回通用的系统错误响应</li>
     * </ol>
     * </p>
     *
     * <p>响应示例：
     * <pre>
     * {
     *     "code": 500,
     *     "message": "系统内部错误",
     *     "data": null
     * }
     * </pre>
     * </p>
     *
     * <p>注意事项：
     * <ul>
     *   <li>此处理器作为兜底处理，捕获所有未被其他处理器处理的异常</li>
     *   <li>为防止敏感信息泄露，对外只返回通用错误消息</li>
     * </ul>
     * </p>
     *
     * @param e 系统异常对象
     * @return 统一格式的系统错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseDTO<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResponseDTO.error(500, "系统内部错误");
    }
}
