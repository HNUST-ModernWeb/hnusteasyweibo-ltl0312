package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.annotation.ExecutionTime;
import cn.edu.hnust.hnusteasyweibo.constant.LogConstants;
import cn.edu.hnust.hnusteasyweibo.service.AuthService;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private LogService logService;

    @Override
    public void logLogin(Long userId, String username, jakarta.servlet.http.HttpServletRequest request, boolean success, String errorMessage) {
        log.info("用户登录: {}", username);

        String requestStatus = success ? LogConstants.REQUEST_STATUS_SUCCESS : LogConstants.REQUEST_STATUS_FAILED;
        String operationDesc = success ? "用户登录" : "用户登录失败: " + errorMessage;

        logService.logOperation(
                userId,
                username,
                LogConstants.OPERATION_TYPE_LOGIN,
                LogConstants.OPERATION_MODULE_USER,
                operationDesc,
                "POST",
                "/api/v1/users/login",
                success ? 200 : 401,
                requestStatus,
                "username=" + username,
                request,
                0.0
        );
    }

    @Override
    @ExecutionTime(value = "userRegister", logOperation = true,
            operationType = LogConstants.OPERATION_TYPE_REGISTER,
            operationModule = LogConstants.OPERATION_MODULE_USER,
            operationDesc = "用户注册",
            requestMethod = "POST",
            requestUrl = "/api/v1/users/register")
    public void logRegister(Long userId, String username, jakarta.servlet.http.HttpServletRequest request) {
        log.info("用户注册: {}", username);
    }

    @Override
    @ExecutionTime(value = "userLogout", logOperation = true,
            operationType = LogConstants.OPERATION_TYPE_LOGOUT,
            operationModule = LogConstants.OPERATION_MODULE_USER,
            operationDesc = "用户登出",
            requestMethod = "POST",
            requestUrl = "/api/v1/users/logout")
    public void logLogout(Long userId, String username, jakarta.servlet.http.HttpServletRequest request) {
        log.info("用户登出: {}", username);
    }
}