package com.example.demouploadanddowloadfile.service.local_save_service;

import com.example.demouploadanddowloadfile.entity.FileDB;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

public interface FileDBService {
    FileDB store(MultipartFile file) throws IOException;
    Stream<FileDB> getAllFiles();
}
