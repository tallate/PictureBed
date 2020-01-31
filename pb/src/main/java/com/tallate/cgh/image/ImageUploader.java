package com.tallate.cgh.image;

import com.tallate.cgh.utils.FileUtil;
import com.tallate.cgh.utils.UtilException;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class ImageUploader {

    @Value("${image.rootPath}")
    private String imageRootPath;

    public String saveFile(MultipartFile file) throws UtilException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传失败，请选择文件");
        }

        String fileName = file.getOriginalFilename();
        File dest = new File(imageRootPath + fileName);
        FileUtil.touch(dest);
        try {
            file.transferTo(dest);
            log.info("上传成功");
            return file.getName();
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
        throw new RuntimeException("上传失败");
    }
}
