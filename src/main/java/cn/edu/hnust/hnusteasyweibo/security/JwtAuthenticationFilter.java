package cn.edu.hnust.hnusteasyweibo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 从HTTP请求头中提取JWT令牌并验证用户身份
 *
 * <p>主要功能：
 * <ul>
 *   <li>从请求头"Authorization"中提取Bearer Token</li>
 *   <li>验证JWT令牌的有效性</li>
 *   <li>将解析出的用户ID存储到请求属性中，供后续处理使用</li>
 *   <li>设置Spring Security的认证对象，确保需要认证的接口可以访问</li>
 * </ul>
 * </p>
 *
 * <p>工作流程：
 * <ol>
 *   <li>请求进入过滤器</li>
 *   <li>从请求头提取Authorization信息</li>
 *   <li>如果存在有效的Bearer Token，解析并提取用户ID</li>
 *   <li>将用户ID存储到request.setAttribute("userId")</li>
 *   <li>设置Spring Security的认证对象</li>
 *   <li>继续执行过滤器链</li>
 * </ol>
 * </p>
 *
 * <p>使用说明：
 * <ul>
 *   <li>继承自OncePerRequestFilter，确保每个请求只执行一次</li>
 *   <li>无需认证的接口（如登录、注册）不受影响</li>
 *   <li>用户ID通过request属性传递，Controller中可通过request.getAttribute("userId")获取</li>
 * </ul>
 * </p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 * @see JwtTokenProvider
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 处理过滤器的主要逻辑
     *
     * <p>功能说明：对每个请求进行JWT认证处理</p>
     *
     * <p>处理流程：
     * <ol>
     *   <li>从请求头获取Authorization信息</li>
     *   <li>验证Token是否有效</li>
     *   <li>如果Token有效，提取用户ID并存入请求属性</li>
     *   <li>设置Spring Security的认证对象</li>
     *   <li>继续执行过滤器链</li>
     * </ol>
     * </p>
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param chain 过滤器链
     * @throws ServletException 若处理过程中发生Servlet异常
     * @throws IOException 若处理过程中发生IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 从请求头获取token
        String token = getTokenFromRequest(request);

        // 验证token
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 从token中获取用户ID
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            // 将用户ID存储到请求中
            request.setAttribute("userId", userId);
            
            // 创建用户详情对象
            UserDetails userDetails = User.builder()
                    .username("user") // 这里可以从token中获取真实用户名
                    .password("")
                    .authorities(Collections.emptyList())
                    .build();
            
            // 创建认证对象
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            
            // 设置认证对象到SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    /**
     * 从请求头中提取JWT Token
     *
     * <p>功能说明：从HTTP Authorization请求头中提取Bearer Token</p>
     *
     * <p>请求头格式：
     * <pre>
     * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     * </pre>
     * </p>
     *
     * @param request HTTP请求对象
     * @return JWT Token字符串；如果不存在或格式不正确返回null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}