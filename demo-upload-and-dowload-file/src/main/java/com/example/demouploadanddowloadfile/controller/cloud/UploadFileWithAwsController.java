package com.example.demouploadanddowloadfile.controller.cloud;


import com.example.demouploadanddowloadfile.dto.BaseResponse;
import com.example.demouploadanddowloadfile.service.cloud.UploadFileWithAwsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/media-file")
@RequiredArgsConstructor
public class UploadFileWithAwsController {

    private final UploadFileWithAwsService service;


    @PostMapping(path = "")
    public BaseResponse<?> saveFile(@RequestParam("title") String title,
                                    @RequestParam("description") String description,
                                    @RequestParam("file") MultipartFile file,
                                    @RequestParam("userId") String userId
                                    ) {
        return service.saveFile(title, description, file, userId);
    }

    @GetMapping(path = "")
    public ResponseEntity<byte[]> download (@RequestParam("path") String path,
                                            @RequestParam("mode") String mode) throws Exception {
        byte[] result =  service.downloadFile(path, mode);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "example.png" + "\"")
                .body(result);
    }

    @PostMapping(path = "/multipart")
    public BaseResponse<?> saveFileMultiPart(@RequestParam("file") MultipartFile file,
                                             @RequestParam("userId") String userId
    ) {
        return service.saveFileMultipart(file, userId);
    }

    @DeleteMapping(path = "")
    public BaseResponse<?> saveFileMultiPart(@RequestParam("path") String path
    ) {
        return service.deleteObject(path);
    }

}
