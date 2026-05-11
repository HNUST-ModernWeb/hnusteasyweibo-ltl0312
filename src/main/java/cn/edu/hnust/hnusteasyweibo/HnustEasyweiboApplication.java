package cn.edu.hnust.hnusteasyweibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 湖南科技大学简易微博系统主应用程序类
 * 基于Spring Boot和Vue的简易微博系统，提供用户注册登录、微博发布、评论点赞等功能
 */
@SpringBootApplication
public class HnustEasyweiboApplication {

    /**
     * 启动Spring Boot应用程序入口
     */
    public static void main(String[] args) {
        SpringApplication.run(HnustEasyweiboApplication.class, args);
    }

}
