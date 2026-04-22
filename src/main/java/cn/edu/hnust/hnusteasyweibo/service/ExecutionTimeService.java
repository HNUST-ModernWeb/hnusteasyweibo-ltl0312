package cn.edu.hnust.hnusteasyweibo.service;

public interface ExecutionTimeService {
    double getExecutionTime(String methodName);

    String getFormattedExecutionTime(String methodName);
}