package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.Data;

/**
 * 统一响应DTO（Data Transfer Object）
 * 用于封装所有API接口的响应数据，保证前后端交互格式统一
 *
 * <p>响应格式示例：
 * <pre>
 * {
 *     "code": 200,
 *     "message": "success",
 *     "data": { ... }
 * }
 * </pre>
 * </p>
 *
 * <p>常用状态码说明：
 * <ul>
 *   <li>200 - 成功</li>
 *   <li>400 - 请求参数错误</li>
 *   <li>401 - 未授权/登录失效</li>
 *   <li>403 - 权限不足</li>
 *   <li>404 - 资源不存在</li>
 *   <li>500 - 服务器内部错误</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：所有Controller接口的返回值类型</p>
 *
 * @param <T> 响应数据的泛型类型
 * @author hnust-easyweibo
 * @version 1.0
 */
@Data
public class ResponseDTO<T> {
    /**
     * 响应状态码
     * 用于表示请求的处理结果
     */
    private int code;

    /**
     * 响应消息
     * 用于描述响应状态或错误信息
     */
    private String message;

    /**
     * 响应数据
     * 业务数据的载体，类型由泛型T指定
     */
    private T data;

    /**
     * 创建成功响应
     *
     * <p>功能说明：创建一个表示成功操作的响应对象，状态码固定为200</p>
     *
     * @param <T> 响应数据的泛型类型
     * @param data 业务数据
     * @return 包含成功状态的ResponseDTO实例
     */
    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    /**
     * 创建错误响应
     *
     * <p>功能说明：创建一个表示错误操作的响应对象，可自定义状态码和错误消息</p>
     *
     * @param <T> 响应数据的泛型类型
     * @param code 错误状态码
     * @param message 错误消息
     * @return 包含错误状态的ResponseDTO实例
     */
    public static <T> ResponseDTO<T> error(int code, String message) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
