package com.example.studentselectionsystem.service.impl;

import com.example.studentselectionsystem.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir:/tmp/uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/api/files}")
    private String fileBaseUrl;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, "files");
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file, "images");
    }

    @Override
    public InputStream downloadFile(String fileUrl) throws IOException {
        String filePath = getFilePathFromUrl(fileUrl);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return new FileInputStream(file);
    }

    @Override
    public Long getFileSize(String fileUrl) throws IOException {
        String filePath = getFilePathFromUrl(fileUrl);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return file.length();
    }

    @Override
    public boolean deleteFile(String fileUrl) throws IOException {
        String filePath = getFilePathFromUrl(fileUrl);
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 通用文件上传方法
     * @param file 要上传的文件
     * @param subDir 子目录
     * @return 文件URL
     */
    private String uploadFile(MultipartFile file, String subDir) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // 保存文件
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // 返回文件URL
        return fileBaseUrl + "/" + subDir + "/" + uniqueFilename;
    }

    /**
     * 从文件URL获取文件路径
     * @param fileUrl 文件URL
     * @return 文件路径
     */
    private String getFilePathFromUrl(String fileUrl) {
        if (fileUrl.startsWith(fileBaseUrl)) {
            String relativePath = fileUrl.substring(fileBaseUrl.length() + 1);
            return Paths.get(uploadDir, relativePath).toString();
        }
        // 如果是相对路径，直接返回
        return fileUrl;
    }
}
