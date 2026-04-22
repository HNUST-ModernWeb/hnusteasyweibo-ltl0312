package cn.edu.hnust.hnusteasyweibo.security;

import cn.edu.hnust.hnusteasyweibo.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌生成和验证工具类
 * 提供JWT令牌的生成、验证和解析功能
 *
 * <p>主要功能：
 * <ul>
 *   <li>根据用户信息生成JWT令牌</li>
 *   <li>验证JWT令牌的有效性</li>
 *   <li>从JWT令牌中提取用户名和用户ID</li>
 * </ul>
 * </p>
 *
 * <p>技术说明：
 * <ul>
 *   <li>使用JJWT库实现JWT功能</li>
 *   <li>签名算法：HS512（需要至少512位的密钥）</li>
 *   <li>令牌有效期：从配置文件中读取（默认7天）</li>
 * </ul>
 * </p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成JWT令牌
     *
     * <p>功能说明：根据用户信息生成包含用户身份验证数据的JWT令牌</p>
     *
     * <p>令牌包含的Claims：
     * <ul>
     *   <li>userId：用户ID</li>
     *   <li>username：用户名</li>
     *   <li>role：用户角色</li>
     *   <li>sub：用户名（作为JWT的主题）</li>
     *   <li>iat：令牌签发时间</li>
     *   <li>exp：令牌过期时间</li>
     * </ul>
     * </p>
     *
     * @param user 包含用户ID、用户名、角色的用户对象
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 验证JWT令牌
     *
     * <p>功能说明：验证令牌是否合法，检查签名和过期时间</p>
     *
     * <p>验证内容：
     * <ul>
     *   <li>签名是否正确</li>
     *   <li>令牌是否过期</li>
     *   <li>令牌格式是否正确</li>
     * </ul>
     * </p>
     *
     * @param token JWT令牌字符串
     * @return true表示令牌有效，false表示令牌无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取用户名
     *
     * <p>功能说明：从JWT令牌的Claims中提取用户名（Subject）</p>
     *
     * @param token JWT令牌字符串
     * @return 用户名
     * @throws Exception 若令牌无效或解析失败
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                .getBody().getSubject();
    }

    /**
     * 从令牌中获取用户ID
     *
     * <p>功能说明：从JWT令牌的Claims中提取用户ID</p>
     *
     * @param token JWT令牌字符串
     * @return 用户ID
     * @throws Exception 若令牌无效或解析失败
     */
    public Long getUserIdFromToken(String token) {
        Object userIdObj = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                .getBody().get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        } else {
            throw new IllegalArgumentException("Invalid userId type in token");
        }
    }
}