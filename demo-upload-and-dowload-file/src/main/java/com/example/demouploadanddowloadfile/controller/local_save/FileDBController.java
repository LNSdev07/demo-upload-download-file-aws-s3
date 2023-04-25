package com.example.demouploadanddowloadfile.controller.local_save;

import com.example.demouploadanddowloadfile.dto.local_save.FileDBResponse;
import com.example.demouploadanddowloadfile.dto.local_save.MessageResponse;
import com.example.demouploadanddowloadfile.entity.FileDB;
import com.example.demouploadanddowloadfile.repository.FileDBRepository;
import com.example.demouploadanddowloadfile.service.local_save_service.FileDBService;
import com.example.demouploadanddowloadfile.service.resize.ResizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileDBController {
    @Autowired
    private FileDBRepository fileDBRepository;

    @Autowired
    private ResizeService resizeService;

    @Autowired
    private FileDBService fileDBService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            fileDBService.store(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileDBResponse>> getListFiles() {
        List<FileDBResponse> files = fileDBService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/auth/files/")
                    .path(dbFile.getId().toString())
                    .toUriString();
            return new FileDBResponse(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getData().length);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id,
                                          @RequestParam("mode") String mode) throws Exception {
        Optional<FileDB> optionalFileDB = fileDBRepository.findById(id);
        byte[] result = resizeService.resizeByMode(optionalFileDB.get().getData(),mode);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + optionalFileDB.get().getName() + "\"")
                .body(result);
    }
}
