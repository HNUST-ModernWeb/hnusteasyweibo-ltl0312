package cn.edu.hnust.hnusteasyweibo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.avatar-dir}")
    private String avatarDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录的绝对路径
        String projectRoot = System.getProperty("user.dir");
        // 构建绝对路径
        java.nio.file.Path absoluteUploadDir = java.nio.file.Paths.get(projectRoot, uploadDir.replace("./", ""));
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadDir.toAbsolutePath() + "/");
    }
}
