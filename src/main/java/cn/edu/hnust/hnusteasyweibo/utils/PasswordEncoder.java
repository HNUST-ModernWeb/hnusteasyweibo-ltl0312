package cn.edu.hnust.hnusteasyweibo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 * 使用Spring Security的BCryptPasswordEncoder实现密码的BCrypt加密和验证功能
 */
@Component
public class PasswordEncoder {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 使用BCrypt算法对明文密码进行加密
     */
    public String encode(String password) {
        return encoder.encode(password);
    }

    /**
     * 验证明文密码与加密后的密码是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
