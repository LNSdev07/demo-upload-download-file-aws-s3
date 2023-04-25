package com.example.demouploadanddowloadfile.controller.cloud;


import com.example.demouploadanddowloadfile.dto.BaseResponse;
import com.example.demouploadanddowloadfile.service.cloud.UploadFileWithAwsPresignedUrlsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UploadFileWithAwsPresignedUrlsController {

    @Autowired
    private UploadFileWithAwsPresignedUrlsService service;


    @GetMapping("/create-resigned-urls-put")
    public BaseResponse<?> generateResignedUrl(@RequestParam("userId") String userId,
                                               @RequestParam("fileName") String fileName,
                                               @RequestParam("type") String type) {
        return service.generatePreSignedUrl(userId, fileName, type);
    }

    @GetMapping("/create-presigned-url-get")
    public BaseResponse<?> generateResignedUrlMethodGet(@RequestParam("filePath") String path) {
        return service.generatePreSignedUrlMethod(path);
    }

}
