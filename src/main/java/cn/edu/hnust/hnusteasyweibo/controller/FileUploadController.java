package cn.edu.hnust.hnusteasyweibo.controller;

import cn.edu.hnust.hnusteasyweibo.constant.LogConstants;
import cn.edu.hnust.hnusteasyweibo.dto.ResponseDTO;
import cn.edu.hnust.hnusteasyweibo.model.User;
import cn.edu.hnust.hnusteasyweibo.service.LogService;
import cn.edu.hnust.hnusteasyweibo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    private LogService logService;

    @Autowired
    private UserService userService;

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

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = resolveUsername(userId);
        try {
            String projectRoot = System.getProperty("user.dir");

            Path filePath = Paths.get(projectRoot, uploadDir.replace("./", ""), filename);
            File file = filePath.toFile();

            if (!file.exists()) {
                filePath = Paths.get(projectRoot, avatarDir.replace("./", ""), filename);
                file = filePath.toFile();
                if (!file.exists()) {
                    logService.logOperation(
                            userId,
                            username,
                            LogConstants.OPERATION_TYPE_DOWNLOAD,
                            LogConstants.OPERATION_MODULE_FILE,
                            "下载文件失败：文件不存在",
                            "GET",
                            "/api/v1/files/download/" + filename,
                            404,
                            LogConstants.REQUEST_STATUS_FAILED,
                            "filename=" + filename,
                            request,
                            0.0
                    );
                    return ResponseEntity.notFound().build();
                }
            }

            FileSystemResource resource = new FileSystemResource(file);
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            logService.logOperation(
                    userId,
                    username,
                    LogConstants.OPERATION_TYPE_DOWNLOAD,
                    LogConstants.OPERATION_MODULE_FILE,
                    "下载文件成功：" + filename,
                    "GET",
                    "/api/v1/files/download/" + filename,
                    200,
                    LogConstants.REQUEST_STATUS_SUCCESS,
                    "filename=" + filename,
                    request,
                    0.0
            );
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            logService.logOperation(
                    userId,
                    username,
                    LogConstants.OPERATION_TYPE_DOWNLOAD,
                    LogConstants.OPERATION_MODULE_FILE,
                    "下载文件异常：" + e.getMessage(),
                    "GET",
                    "/api/v1/files/download/" + filename,
                    500,
                    LogConstants.REQUEST_STATUS_ERROR,
                    "filename=" + filename,
                    request,
                    0.0
            );
            return ResponseEntity.internalServerError().build();
        }
    }

    private String resolveUsername(Long userId) {
        if (userId == null) {
            return "anonymous";
        }
        try {
            User user = userService.getUserById(userId);
            return user.getUsername();
        } catch (Exception e) {
            return String.valueOf(userId);
        }
    }

    private String saveFile(MultipartFile file, String baseDir) throws IOException {
        String projectRoot = System.getProperty("user.dir");
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
