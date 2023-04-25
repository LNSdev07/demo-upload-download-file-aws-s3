package com.example.demouploadanddowloadfile.service.resize;


import net.coobird.thumbnailator.Thumbnails;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResizeService {
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .size(targetWidth, targetHeight)
                .outputFormat("JPEG")
                .outputQuality(1)
                .toOutputStream(outputStream);
        byte[] data = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return ImageIO.read(inputStream);
    }

    public byte[] resizeByMode(byte[] originalFile, String mode) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(originalFile);
        BufferedImage originalImage = ImageIO.read(inputStream);

        int targetWidth = mode.equals("phone")? 720: 1280; // hoặc 1080 tùy vào nhu cầu
        double ratio = (double) targetWidth / originalImage.getWidth();
        int targetHeight = (int) (originalImage.getHeight() * ratio);

        BufferedImage resizedImage = resizeImage(originalImage, targetWidth, targetHeight);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", outputStream);

        return outputStream.toByteArray();
    }


    public MultipartFile resizeImageAndVideo(MultipartFile file, int width, int height) throws IOException {
//         Tạo file mới có kích thước mong muốn
        Path tempFile = Files.createTempFile("temp", file.getOriginalFilename());
        Mat newImage = new Mat(height, width, 100);
        Imgproc.resize(Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_COLOR), newImage, new Size(width, height));

        // Ghi file mới vào temp file
        Imgcodecs.imwrite(tempFile.toString(), newImage);

//         Convert file mới thành MultipartFile
        MultipartFile resizedFile = new MultipartFile() {
            @Override
            public String getName() {
                return file.getName();
            }

            @Override
            public String getOriginalFilename() {
                return file.getOriginalFilename();
            }

            @Override
            public String getContentType() {
                return file.getContentType();
            }

            @Override
            public boolean isEmpty() {
                return file.isEmpty();
            }

            @Override
            public long getSize() {
                return file.getSize();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return Files.readAllBytes(tempFile);
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return Files.newInputStream(tempFile);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                Files.copy(tempFile, Paths.get(dest.getAbsolutePath()));
            }
        };

        return resizedFile;
    }

}
