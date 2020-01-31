package com.tallate.cgh.image;

import com.google.common.collect.Lists;
import com.tallate.cgh.utils.FileUtil;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageRefresher implements InitializingBean {

    @Value("${image.rootPath}")
    private String imageRootPath;

    /**
     * 所有图片的路径
     */
    private List<String> imagePaths;

    private ScheduledExecutorService refreshingExecutor;

    private void loadImagePaths() {
        String systemEncoding = System.getProperty("file.encoding");
        File[] files = FileUtil.listFiles(imageRootPath);
        // 过滤图片的后缀
        files = Arrays.stream(files)
                .filter(file -> file.getName().matches(".*\\.(jpg|jpeg|png)"))
                .toArray(File[]::new);
        if (files.length == 0) {
            imagePaths = Lists.newArrayList();
            log.info("imagePaths:{}", imagePaths);
            return;
        }
        List<String> imagePaths = Arrays.stream(files)
                .map(file -> {
                    String path = file.getAbsolutePath().replace(imageRootPath, "");
                    try {
                        return new String(path.getBytes(systemEncoding), StandardCharsets.UTF_8);
                    } catch (UnsupportedEncodingException e) {
                        log.warn("编码失败", e);
                    }
                    return "ERROR";
                })
                .collect(Collectors.toList());
        log.info("imagePaths:{}", imagePaths);
        System.out.println(imagePaths != null && imagePaths.size() > 0 ? "imagePath: " + imagePaths.get(14) : "");
        this.imagePaths = imagePaths;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshingExecutor = new ScheduledThreadPoolExecutor(1,
                (runnable) -> new Thread(runnable, "refreshingExecutor"));
        refreshingExecutor.scheduleAtFixedRate(this::loadImagePaths,
                1, 1, TimeUnit.SECONDS);
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }
}
