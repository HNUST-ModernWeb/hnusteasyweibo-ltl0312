package cn.edu.hnust.hnusteasyweibo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 * 提供密码的BCrypt加密和验证功能
 *
 * <p>主要功能：
 * <ul>
 *   <li>使用BCrypt算法对密码进行加密存储</li>
 *   <li>验证用户登录时输入的密码是否正确</li>
 * </ul>
 * </p>
 *
 * <p>技术说明：
 * <ul>
 *   <li>使用Spring Security的BCryptPasswordEncoder</li>
 *   <li>BCrypt是一种自适应哈希函数，可以抵抗彩虹表攻击</li>
 *   <li>每次加密结果都不同（因为使用了随机盐）</li>
 *   <li>验证时通过BCrypt的matches方法自动处理盐值</li>
 * </ul>
 * </p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Component
public class PasswordEncoder {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     *
     * <p>功能说明：使用BCrypt算法对明文密码进行加密</p>
     *
     * <p>特点：
     * <ul>
     *   <li>每次调用都会生成不同的加密结果（因为使用随机盐）</li>
     *   <li>相同的密码每次加密结果不同，但验证时都能匹配成功</li>
     * </ul>
     * </p>
     *
     * @param password 明文密码
     * @return 加密后的密码字符串
     */
    public String encode(String password) {
        return encoder.encode(password);
    }

    /**
     * 验证密码
     *
     * <p>功能说明：验证明文密码与加密后的密码是否匹配</p>
     *
     * <p>特点：
     * <ul>
     *   <li>自动处理BCrypt加密时使用的盐值</li>
     *   <li>无需手动处理盐值的提取和比较</li>
     * </ul>
     * </p>
     *
     * @param rawPassword 明文密码（用户输入）
     * @param encodedPassword 加密后的密码（存储在数据库中）
     * @return true表示密码匹配，false表示不匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
