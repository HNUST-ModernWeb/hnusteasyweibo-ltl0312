package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class FileLogServiceImpl implements LogService {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter FILE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmssSSS");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${log.base-dir}")
    private String logBaseDir;

    @Override
    public void ensureUserDirectory(Long userId) {
        try {
            Files.createDirectories(resolveUserDir(userId));
        } catch (IOException e) {
            log.error("创建用户日志目录失败: {}", e.getMessage());
        }
    }

    @Override
    public void ensureDailyDirectory(Long userId) {
        try {
            Files.createDirectories(resolveDayDir(userId));
        } catch (IOException e) {
            log.error("创建用户当日日志目录失败: {}", e.getMessage());
        }
    }

    @Override
    public void logOperation(
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
    ) {
        writeOperationLog(userId, username, operationType, operationModule, operationDesc, requestMethod,
                requestUrl, responseStatus, requestStatus, requestParams, request, 0.0, methodName);
    }

    @Override
    public void logOperation(
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
    ) {
        writeOperationLog(userId, username, operationType, operationModule, operationDesc, requestMethod,
                requestUrl, responseStatus, requestStatus, requestParams, request, executionTime, null);
    }

    private void writeOperationLog(
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
            double executionTime,
            String methodName
    ) {
        try {
            Path dayDir = resolveDayDir(userId);
            Files.createDirectories(dayDir);

            Map<String, Object> logEntry = new LinkedHashMap<>();
            logEntry.put("userId", userId != null ? userId : 0L);
            logEntry.put("username", safe(username));
            logEntry.put("operationType", safe(operationType));
            logEntry.put("operationModule", safe(operationModule));
            logEntry.put("operationDesc", safe(operationDesc));
            logEntry.put("requestMethod", safe(requestMethod));
            logEntry.put("requestUrl", safe(requestUrl));
            logEntry.put("requestParams", safe(requestParams));
            logEntry.put("requestStatus", safe(requestStatus));
            logEntry.put("responseStatus", responseStatus != null ? responseStatus : 0);
            logEntry.put("executionTimeMs", round2(executionTime));
            logEntry.put("methodName", safe(methodName));
            logEntry.put("clientIp", resolveClientIp(request));
            logEntry.put("userAgent", resolveUserAgent(request));
            logEntry.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));

            String fileName = LocalDateTime.now().format(FILE_TIME_FORMATTER)
                    + "_"
                    + sanitizeForFile(operationType)
                    + "_"
                    + UUID.randomUUID().toString().substring(0, 8)
                    + ".json";

            Path logFile = dayDir.resolve(fileName);
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logEntry);
            Files.writeString(logFile, json, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (Exception e) {
            log.error("写入日志文件失败: {}", e.getMessage(), e);
        }
    }

    private Path resolveUserDir(Long userId) {
        return Paths.get(logBaseDir, resolveUserDirName(userId));
    }

    private Path resolveDayDir(Long userId) {
        return resolveUserDir(userId).resolve(LocalDate.now().format(DAY_FORMATTER));
    }

    private String resolveUserDirName(Long userId) {
        return String.valueOf(userId != null ? userId : 0L);
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "";
    }

    private String resolveUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "";
    }

    private String sanitizeForFile(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
