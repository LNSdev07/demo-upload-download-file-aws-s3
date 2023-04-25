package com.example.demouploadanddowloadfile.service.cloud.impl;


import com.amazonaws.AmazonClientException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.example.demouploadanddowloadfile.dto.BaseResponse;
import com.example.demouploadanddowloadfile.service.cloud.UploadFileWithAwsPresignedUrlsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadFileWithAwsPresignedUrlsServiceImpl implements UploadFileWithAwsPresignedUrlsService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    public BaseResponse<?> generatePreSignedUrl(String userId, String fileName, String type) {
        try {
            if (!Arrays.asList("png", "gif", "mp4", "jpeg")
                    .contains(type)) {
                return BaseResponse.builder().status(500).message("format not support!")
                        .build();
            }
            String path = String.format("%s/%s.%s", userId, new Date().getTime() + "-" +fileName, type);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, 10); //validity of 10 minutes
            String urls = amazonS3.generatePresignedUrl(bucketName, path, calendar.getTime(), HttpMethod.PUT).toString();
            return BaseResponse.builder().status(200).data(urls).message("SUCCESS")
                    .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return BaseResponse.builder().status(500).message("FAIL")
                    .build();
        }
    }

    public BaseResponse<?> generatePreSignedUrlMethod(String filePath){
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.SECOND, 20);
            String urls =  amazonS3.generatePresignedUrl(bucketName, filePath, calendar.getTime(), HttpMethod.GET).toString();
            return BaseResponse.builder().status(200).data(urls).message("SUCCESS")
                    .build();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return BaseResponse.builder().status(500).message("FAIL")
                    .build();
        }
    }


}
