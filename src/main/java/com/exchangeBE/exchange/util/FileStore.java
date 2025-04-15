package com.exchangeBE.exchange.util;

import com.exchangeBE.exchange.entity.trip.UploadFileEntity;
import com.exchangeBE.exchange.repository.trip.UploadFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    private final UploadFileRepository uploadFileRepository;

    public FileStore(UploadFileRepository uploadFileRepository) {
        this.uploadFileRepository = uploadFileRepository;
    }

    //@Value("${file.dir}") // application.properties에 파일 저장 경로 설정
    private String fileDir = "/Desktop/";

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFileEntity> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFileEntity> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadFileEntity storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        // 파일 정보를 데이터베이스에 저장
        UploadFileEntity uploadFile = new UploadFileEntity(originalFilename, storeFileName);
        uploadFileRepository.save(uploadFile);

        return uploadFile;
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
