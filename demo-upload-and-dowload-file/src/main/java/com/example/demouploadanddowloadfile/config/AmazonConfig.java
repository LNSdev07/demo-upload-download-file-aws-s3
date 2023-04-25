package com.example.demouploadanddowloadfile.config;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import static com.amazonaws.services.s3.internal.Constants.MB;


@Configuration
@Slf4j
public class AmazonConfig {

    @Value("${access_key}")
    private  String accessKey;

    @Value("${secret_key}")
    private String secretKey;

    @Value("${amazon.aws.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Bean
    public AmazonS3 s3(){
        AWSCredentials awsCredentials  = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    @Bean
    public TransferManager transferManager(){

        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(s3())
                .withDisableParallelDownloads(false)
                .withMinimumUploadPartSize((long) (5 * MB))
                .withMultipartUploadThreshold((long) (16 * MB))
                .withMultipartCopyPartSize((long) (5 * MB))
                .withMultipartCopyThreshold((long) (100 * MB))
                .withExecutorFactory(()->createExecutorService(20))
                .build();

        int oneDay = 1000 * 60 * 60 * 24;
        Date oneDayAgo = new Date(System.currentTimeMillis() - oneDay);

        try {

            tm.abortMultipartUploads(bucketName, oneDayAgo);

        } catch (AmazonClientException e) {
            log.error("Unable to upload file, upload was aborted, reason: " + e.getMessage());
        }

        return tm;
    }

    private ThreadPoolExecutor createExecutorService(int threadNumber) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int threadCount = 1;

            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("jsa-amazon-s3-transfer-manager-worker-" + threadCount++);
                return thread;
            }
        };
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber, threadFactory);
    }

}
