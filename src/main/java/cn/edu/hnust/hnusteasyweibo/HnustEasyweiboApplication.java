package cn.edu.hnust.hnusteasyweibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 湖南科技大学简易微博系统主应用程序类
 *
 * <p>项目简介：
 * 本项目是一个基于Spring Boot和Vue的简易微博系统，提供用户注册登录、微博发布、评论点赞等功能
 * </p>
 *
 * <p>主要技术栈：
 * <ul>
 *   <li>后端：Spring Boot 4.0.5, MyBatis, Spring Security, JWT</li>
 *   <li>前端：Vue 3, Element Plus, Vuex, Vue Router</li>
 *   <li>数据库：MySQL</li>
 *   <li>认证：JWT (JSON Web Token)</li>
 * </ul>
 * </p>
 *
 * <p>功能模块：
 * <ul>
 *   <li>用户模块：注册、登录、个人信息管理</li>
 *   <li>微博模块：发布、查看、删除微博</li>
 *   <li>评论模块：评论、回复微博</li>
 *   <li>社交模块：关注、粉丝、点赞</li>
 *   <li>日志模块：操作日志记录与审计</li>
 * </ul>
 * </p>
 *
 * <p>启动说明：
 * <ul>
 *   <li>确保MySQL数据库已启动并创建hnusteasyweibo数据库</li>
 *   <li>配置application.yml中的数据库连接信息</li>
 *   <li>运行main方法启动应用程序</li>
 *   <li>后端服务默认端口：8080</li>
 * </ul>
 * </p>
 *
 * @author hnust-easyweibo
 * @version 1.0
 */
@SpringBootApplication
public class HnustEasyweiboApplication {

    /**
     * 应用程序入口方法
     *
     * <p>功能说明：启动Spring Boot应用程序</p>
     *
     * <p>启动流程：
     * <ol>
     *   <li>SpringApplication.run()初始化Spring应用上下文</li>
     *   <li>自动扫描并注册Spring组件（Controller、Service、Mapper等）</li>
     *   <li>启动内嵌的Tomcat服务器</li>
     *   <li>应用程序开始接收HTTP请求</li>
     * </ol>
     * </p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(HnustEasyweiboApplication.class, args);
    }

}
