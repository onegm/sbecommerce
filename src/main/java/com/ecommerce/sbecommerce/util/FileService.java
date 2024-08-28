package com.ecommerce.sbecommerce.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileService {
    public static String uploadImage(String dirPath, MultipartFile file) {
        String uniqueId = UUID.randomUUID().toString();
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        String fileName = uniqueId.concat(fileExtension);

        String filePath = dirPath + File.separator + fileName; // Separator avoids problems with OS specific separators

        File imagesFolder = new File(dirPath);
        if(!imagesFolder.exists())
            imagesFolder.mkdir();

        try {
            Files.copy(file.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }
}
