package com.example.studentselectionsystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传文件
     * @param file 要上传的文件
     * @return 文件URL
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 上传图片
     * @param file 要上传的图片文件
     * @return 图片URL
     */
    String uploadImage(MultipartFile file) throws IOException;

    /**
     * 下载文件
     * @param fileUrl 文件URL
     * @return 文件输入流
     */
    InputStream downloadFile(String fileUrl) throws IOException;

    /**
     * 获取文件大小
     * @param fileUrl 文件URL
     * @return 文件大小（字节）
     */
    Long getFileSize(String fileUrl) throws IOException;

    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl) throws IOException;
}
