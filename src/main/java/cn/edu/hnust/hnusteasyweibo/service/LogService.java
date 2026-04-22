package cn.edu.hnust.hnusteasyweibo.service;

import jakarta.servlet.http.HttpServletRequest;

public interface LogService {
    void ensureUserDirectory(Long userId);

    void ensureDailyDirectory(Long userId);

    void logOperation(
            Long userId,
            String username,
            String operationType,
            String operationModule,
            String operationDesc,
            String requestMethod,
            String requestUrl,
            Integer responseStatus,
            String requestStatus,
            String requestParams,
            HttpServletRequest request,
            String methodName
    );

    void logOperation(
            Long userId,
            String username,
            String operationType,
            String operationModule,
            String operationDesc,
            String requestMethod,
            String requestUrl,
            Integer responseStatus,
            String requestStatus,
            String requestParams,
            HttpServletRequest request,
            double executionTime
    );
}
