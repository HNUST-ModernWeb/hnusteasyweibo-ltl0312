package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.Data;

/**
 * 统一响应DTO
 * 用于封装所有API接口的响应数据，保证前后端交互格式统一
 *
 * @param <T> 响应数据的泛型类型
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
     * 创建成功响应，状态码固定为200
     */
    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    /**
     * 创建错误响应，可自定义状态码和错误消息
     */
    public static <T> ResponseDTO<T> error(int code, String message) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
