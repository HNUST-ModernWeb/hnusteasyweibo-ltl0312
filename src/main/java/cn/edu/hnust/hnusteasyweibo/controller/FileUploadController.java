package cn.edu.hnust.hnusteasyweibo.controller;

import cn.edu.hnust.hnusteasyweibo.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.avatar-dir}")
    private String avatarDir;

    @PostMapping("/upload")
    public ResponseDTO<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseDTO.error(400, "请选择文件");
        }
        if (file.getSize() > 50 * 1024 * 1024) {
            return ResponseDTO.error(400, "文件大小不能超过50MB");
        }

        try {
            String url = saveFile(file, uploadDir);
            return ResponseDTO.success(url);
        } catch (IOException e) {
            return ResponseDTO.error(500, "文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public ResponseDTO<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseDTO.error(400, "请选择头像文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseDTO.error(400, "只能上传图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseDTO.error(400, "头像文件大小不能超过5MB");
        }

        try {
            String url = saveFile(file, avatarDir);
            return ResponseDTO.success(url);
        } catch (IOException e) {
            return ResponseDTO.error(500, "头像上传失败: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file, String baseDir) throws IOException {
        // 获取项目根目录的绝对路径
        String projectRoot = System.getProperty("user.dir");
        // 构建绝对路径
        Path dirPath = Paths.get(projectRoot, baseDir.replace("./", ""));
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        Path filePath = dirPath.resolve(newFilename);
        file.transferTo(filePath.toFile());

        return "/uploads/" + (baseDir.equals(avatarDir) ? "avatars/" : "") + newFilename;
    }
}
