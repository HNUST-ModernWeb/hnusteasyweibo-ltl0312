package cn.edu.hnust.hnusteasyweibo.constant;

/**
 * 日志常量类
 * 定义操作日志中使用的操作类型、操作模块等常量值
 */
public class LogConstants {
    public static final String OPERATION_TYPE_LOGIN = "LOGIN";
    public static final String OPERATION_TYPE_REGISTER = "REGISTER";
    public static final String OPERATION_TYPE_LOGOUT = "LOGOUT";
    public static final String OPERATION_TYPE_CREATE = "CREATE";
    public static final String OPERATION_TYPE_UPDATE = "UPDATE";
    public static final String OPERATION_TYPE_DELETE = "DELETE";
    public static final String OPERATION_TYPE_READ = "READ";
    public static final String OPERATION_TYPE_QUERY = "QUERY";
    public static final String OPERATION_TYPE_DOWNLOAD = "DOWNLOAD";

    public static final String OPERATION_MODULE_USER = "USER";
    public static final String OPERATION_MODULE_POST = "POST";
    public static final String OPERATION_MODULE_COMMENT = "COMMENT";
    public static final String OPERATION_MODULE_LIKE = "LIKE";
    public static final String OPERATION_MODULE_FOLLOW = "FOLLOW";
    public static final String OPERATION_MODULE_SYSTEM = "SYSTEM";
    public static final String OPERATION_MODULE_FILE = "FILE";

    public static final String REQUEST_STATUS_SUCCESS = "SUCCESS";
    public static final String REQUEST_STATUS_FAILED = "FAILED";
    public static final String REQUEST_STATUS_ERROR = "ERROR";

    public static final int REQUEST_STATUS_PROCESSING = 0;
    public static final int REQUEST_STATUS_SUCCESS_NUM = 1;
    public static final int REQUEST_STATUS_FAILED_NUM = 2;

    public static final String OPERATION_DESC_LOGIN = "用户登录";
    public static final String OPERATION_DESC_LOGOUT = "用户登出";
}
