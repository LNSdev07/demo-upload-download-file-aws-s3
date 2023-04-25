package com.example.demouploadanddowloadfile.service.cloud;

import com.example.demouploadanddowloadfile.dto.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UploadFileWithAwsPresignedUrlsService {
    BaseResponse<?> generatePreSignedUrl(String userId, String fileName, String type);
    BaseResponse<?> generatePreSignedUrlMethod(String filePath);
}
