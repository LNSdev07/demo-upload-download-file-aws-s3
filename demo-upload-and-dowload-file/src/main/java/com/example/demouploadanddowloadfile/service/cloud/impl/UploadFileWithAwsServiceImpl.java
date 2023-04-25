package com.example.demouploadanddowloadfile.service.cloud.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.example.demouploadanddowloadfile.dto.BaseResponse;
import com.example.demouploadanddowloadfile.entity.FileDB;
import com.example.demouploadanddowloadfile.repository.FileDBRepository;
import com.example.demouploadanddowloadfile.repository.UserRepository;
import com.example.demouploadanddowloadfile.service.aws_service_s3.FileStoreService;
import com.example.demouploadanddowloadfile.service.cloud.UploadFileWithAwsService;
import com.example.demouploadanddowloadfile.service.resize.ResizeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class UploadFileWithAwsServiceImpl implements UploadFileWithAwsService {


    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private FileStoreService fileStoreService;

    @Autowired
    private ResizeService resizeService;

    private  final AmazonS3 s3;

    @Autowired
    private UserRepository repository;

    @Autowired
    private FileDBRepository fileDBRepository;

    @Autowired
    private TransferManager transferManager;

    @Override
    @Transactional
    public BaseResponse<?> saveFile(String title, String description, MultipartFile fileTmp, String userId) {

        if(!repository.existsById(userId)){
            return BaseResponse.builder().message("USER_ID NOT FOUND!")
                    .status(500).build();
        }
        if (fileTmp.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        if (!Arrays.asList(ContentType.IMAGE_PNG.getMimeType(),
                        ContentType.IMAGE_BMP.getMimeType(),
                        ContentType.IMAGE_GIF.getMimeType(),
                        "video/mp4",
                        ContentType.IMAGE_JPEG.getMimeType())
                .contains(fileTmp.getContentType())) {
            return BaseResponse.builder().status(500).message("format not support!")
                    .build();
        }

        MultipartFile file = fileTmp;
//        try {
//            file = resizeService.resizeImageAndVideo(fileTmp, 10, 10);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        //get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        String path = String.format("%s/%s", bucketName, userId);
        String fileName = String.format("%s", new Date().getTime() +"-"+file.getOriginalFilename());

        try {
            fileStoreService.upload(path, fileName, Optional.of(metadata), file.getInputStream());
            // lưu file vao DB
            FileDB fileDB = new FileDB();
            fileDB.setName(fileName);
            fileDB.setPath((path +"/"+ fileName).substring((bucketName+"/").length()));
            fileDB.setType(file.getContentType());
            fileDB.setUserId(userId);

            fileDBRepository.save(fileDB);

            return BaseResponse.builder().message("UPLOAD SUCCESS")
                    .status(200).build();
        } catch (IOException e) {
            return BaseResponse.builder().message("UPLOAD FAILED")
                    .status(500).build();
        }
    }

    @Override
    public byte[] downloadFile(String path, String mode) throws Exception {
        byte[] rs =  fileStoreService.download(path);
        return resizeService.resizeByMode(rs, mode);
    }

    @Override
    @Transactional
    public BaseResponse<?> saveFileMultipart(MultipartFile file, String userId) {
        if(!repository.existsById(userId)){
            return BaseResponse.builder().message("USER_ID NOT FOUND!")
                    .status(500).build();
        }

        if (!Arrays.asList(ContentType.IMAGE_PNG.getMimeType(),
                        ContentType.IMAGE_BMP.getMimeType(),
                        ContentType.IMAGE_GIF.getMimeType(),
                        "video/mp4",
                        ContentType.IMAGE_JPEG.getMimeType())
                .contains(file.getContentType())) {
            return BaseResponse.builder().status(500).message("format not support!")
                    .build();
        }

        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();
        String path = userId + "/" + (new Date().getTime())+ "-"+fileName;
        long partSize = 5 * 1024 * 1024; // 5MB

        // Initialize the multipart upload
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, path);
        InitiateMultipartUploadResult initResult = s3.initiateMultipartUpload(initRequest);

        // Upload the parts
        List<PartETag> partETags = new ArrayList<>();
        long filePosition = 0;
        for (int i = 1; filePosition < fileSize; i++) {
            // Determine the part size
            long partSizeTemp = Math.min(partSize, fileSize - filePosition);
            // Create the request to upload a part
            UploadPartRequest uploadRequest = null;
            try {
                uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(path)
                        .withUploadId(initResult.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withPartSize(partSizeTemp)
                        .withInputStream(file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            UploadPartResult uploadResult = s3.uploadPart(uploadRequest);
            partETags.add(uploadResult.getPartETag());
            filePosition += partSizeTemp;
        }

        // Complete the multipart upload
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, path,
                initResult.getUploadId(), partETags);
        s3.completeMultipartUpload(compRequest);


        FileDB fileDB = new FileDB();
        fileDB.setName(fileName);
        fileDB.setPath(path);
        fileDB.setType(file.getContentType());
        fileDB.setUserId(userId);

        fileDBRepository.save(fileDB);

        return BaseResponse.builder().message("UPLOAD SUCCESS")
                .status(200).build();
    }

    @Override
    @Transactional
    public BaseResponse<?> deleteObject(String path) {
        try {
            s3.deleteObject(bucketName, path);
            fileDBRepository.deleteByPath(path);
            return BaseResponse.builder().message("Object with key " + path + " has been deleted successfully.")
                    .status(HttpStatus.SC_OK).build();
        } catch (AmazonServiceException e) {
            return BaseResponse.builder().status(e.getStatusCode())
                    .message(e.getMessage())
                    .build();
        }
    }


    @Transactional
    public BaseResponse<?> saveFileMultiPartFileWithTransferManager(MultipartFile file, String userId){
        ObjectMetadata metadata = new ObjectMetadata();
        long totalbytes = file.getSize();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(totalbytes);
        String path = userId+"/"+new Date().getTime()+"-" + file.getOriginalFilename();
        final PutObjectRequest request;
        try {
            request = new PutObjectRequest(bucketName,
                    path,
                    file.getInputStream(),
                    metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Upload upload = transferManager.upload(request);

        try {
            upload.waitForCompletion();
            return BaseResponse.builder().status(200).message("SUCCESS")
                    .build();

        } catch (InterruptedException | AmazonClientException e) {
            log.info(e.getMessage());
            return BaseResponse.builder().status(500).message("FAIL")
                    .build();
        }
    }


}
