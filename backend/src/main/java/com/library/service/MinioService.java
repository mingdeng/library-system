package com.library.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Value("${minio.bucket-name}")
    private String bucketName;
    
    /**
     * 上传文件到 MinIO
     * 
     * @param file 文件
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String objectName) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            
            return getObjectUrl(objectName);
        }
    }
    
    /**
     * 获取文件访问URL
     * 
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String getObjectUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(io.minio.http.Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(7 * 24 * 60 * 60) // 7天过期
                        .build()
        );
    }
    
    /**
     * 删除文件
     * 
     * @param objectName 对象名称
     */
    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }
}
