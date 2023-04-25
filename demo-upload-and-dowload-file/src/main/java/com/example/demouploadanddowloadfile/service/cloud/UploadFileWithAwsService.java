package com.example.demouploadanddowloadfile.service.cloud;

import com.example.demouploadanddowloadfile.dto.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UploadFileWithAwsService {
    BaseResponse<?> saveFile(String title, String description, MultipartFile file, String userId);
    byte[] downloadFile(String path, String mode) throws Exception;

    BaseResponse<?> saveFileMultipart(MultipartFile file, String userId);

    BaseResponse<?> deleteObject(String path);

    BaseResponse<?> saveFileMultiPartFileWithTransferManager(MultipartFile file, String userId);
}
