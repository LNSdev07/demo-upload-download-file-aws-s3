package com.example.demouploadanddowloadfile.service.local_save_service.impl;

import com.example.demouploadanddowloadfile.entity.FileDB;
import com.example.demouploadanddowloadfile.repository.FileDBRepository;
import com.example.demouploadanddowloadfile.service.local_save_service.FileDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;


@Service
public class FileDBServiceImpl implements FileDBService {

    @Autowired
    private FileDBRepository fileDBRepository;

    @Override
    public FileDB store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename()));
        FileDB image = new FileDB(fileName, file.getContentType(), file.getBytes());
        return fileDBRepository.save(image);
    }

    @Override
    public Stream<FileDB> getAllFiles() {
        return fileDBRepository.findAll().stream().filter(x ->x.getData() != null);
    }
}
