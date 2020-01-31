package com.tallate.cgh.controller;

import com.tallate.cgh.image.ImageRefresher;
import com.tallate.cgh.image.ImageUploader;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cgh/image")
@Slf4j
public class CghImageController {

    @Autowired
    private ImageRefresher imageRefresher;

    @Autowired
    private ImageUploader imageUploader;

    @GetMapping(value = "/list")
    @ResponseBody
    public Response<List<String>> list() {
        return Response.success(imageRefresher.getImagePaths());
    }

    @PostMapping("")
    @ResponseBody
    public Response<String> upload(@RequestBody MultipartFile file) {
        try {
            return Response.success(imageUploader.saveFile(file));
        } catch (Exception e) {
            return Response.fail(10000, "上传失败");
        }
    }
}
