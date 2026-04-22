package cn.edu.hnust.hnusteasyweibo.service.impl;

import cn.edu.hnust.hnusteasyweibo.aop.ExecutionTimeAspect;
import cn.edu.hnust.hnusteasyweibo.service.ExecutionTimeService;
import org.springframework.stereotype.Service;

@Service
public class ExecutionTimeServiceImpl implements ExecutionTimeService {

    @Override
    public double getExecutionTime(String methodName) {
        try {
            return ExecutionTimeAspect.getExecutionTime(methodName);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public String getFormattedExecutionTime(String methodName) {
        try {
            return ExecutionTimeAspect.getFormattedExecutionTime(methodName);
        } catch (Exception e) {
            return "0ms";
        }
    }
}