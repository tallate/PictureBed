package com.tallate.cgh.utils;

import com.google.common.io.Resources;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件IO工具
 * 1. 相对项目根路径进行读写
 * File file = new File("file")
 * 2. 相对磁盘根路径进行读写
 * File file = new File("/")
 * 3. 相对classpath路径读写
 * FileUtil.class.getResource("/") 从CLASSPATH根目录下查找文件
 * FileUtil.class.getResource("file") 从该类所在包下查找
 * FileUtil.class.getClassLoader().getResource("file") 从CLASSPATH根目录下查找文件
 */
public class FileUtil {

    /**
     * 不要吐槽磁盘可能不是挂载在根目录的情况
     */
    public static final String DISK_PATH = "";

    /**
     * 项目根目录下
     */
    public static final String PROJECT_PATH = System.getProperty("user.dir");

    /**
     * 这种方法据说在weblogic下会失败，且会多出一个file:前缀
     * public static String CLASS_PATH = FileUtil.class.getResource("/").toString();
     */
    public static final String CLASS_PATH = Thread.currentThread().
            getContextClassLoader().getResource("").getPath();


    /**
     * 备份文件的目录
     *
     * @return /tmp/2018-05-11
     */
    private static String getBackupFilePath() {
        return "/tmp/" + TimeUtil.getCurDate();
    }

    /**
     * 备份文件
     *
     * @param file 文件的绝对路径
     */
    private static void backup(File file) throws UtilException {
        if (!file.exists()) {
            throw new UtilException("File unexists");
        }
        File backupFile = new File(getBackupFilePath() + file.getPath());
        // 不能调用其他读写方法，不然会死循环
        touch(backupFile);
        String data = readFile(file);
        writeFile(backupFile, data);
    }

    public static void mkParentDirs(File file) throws UtilException {
        if (null != file.getParent()) {
            File dir = new File(file.getParent());
            if (!dir.exists() && !dir.mkdirs()) {
                throw new UtilException("创建目录 [" + dir.getName() + "] 失败");
            }
        }
    }

    public static void touch(File file) throws UtilException {
        try {
            if (null != file.getParent()) {
                File dir = new File(file.getParent());
                if (dir.exists() && dir.isFile()) {
                    throw new UtilException("创建备份目录失败:[" + dir.getName() + "] 是一个文件而不是目录");
                }
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new UtilException("创建目录 [" + dir.getName() + "] 失败");
                }
            }
            if (!file.exists() && !file.createNewFile()) {
                throw new UtilException("创建文件 [" + file.getName() + "] 失败");
            }
        } catch (IOException e) {
            throw new UtilException("创建文件失败", e);
        }
        // 这里需要根据虚拟机空闲空间判断
        if (file.length() > Runtime.getRuntime().freeMemory()) {
            throw new UtilException(
                    "文件过大：" + file.length() + " 当前JVM剩余空间：" + Runtime.getRuntime().freeMemory());
        }
    }

    /**
     * 默认从类路径下获取文件Path
     * 来自：https://github.com/spotify/docker-client/blob/master/src/test/java/com/spotify/docker/client/DefaultDockerClientTest.java
     */
    public static Path getResource(String name) throws UtilException {
        // Resources.getResources(...).getPath() does not work correctly on windows,
        // hence this workaround.  See: https://github.com/spotify/docker-client/pull/780
        // for details
        try {
            return Paths.get(Resources.getResource(name).toURI());
        } catch (URISyntaxException e) {
            throw new UtilException("Get file/directory resource failed.cause:" + e.getMessage(), e);
        }
    }

    /**
     * 从指定的类路径下加载资源，可以实现从test-classes下读取资源
     * 以"/"开头，从classPath根路径下读取资源
     * 不以"/"开头，从该类的路径下读取资源
     */
    public static Path getResource(Class contextClass, String name) throws UtilException {
        try {
            return Paths.get(Resources.getResource(contextClass, name).toURI());
        } catch (URISyntaxException e) {
            throw new UtilException("Get file/directory resource failed.cause:" + e.getMessage(), e);
        }
    }

    /**
     * 从项目根路径下读取资源
     */
    public static Path getResourceFromProject(String name) throws UtilException {
        if (!name.startsWith("/")) {
            throw new UtilException("文件路径必须以/开头");
        }
        return Paths.get(PROJECT_PATH + name);
    }

    /**
     * 从文件系统读取资源
     */
    public static Path getResourceFromDisk(String name) throws UtilException {
        if (!name.startsWith("/")) {
            throw new UtilException("文件路径必须以/开头");
        }
        return Paths.get(DISK_PATH + name);
    }

    /**
     * 打开文件或目录输入流
     */
    public static InputStream openInputStream(String fileName) throws UtilException {
        try {
            File file = new File(fileName);
            InputStream is = new FileInputStream(file);
            return new BufferedInputStream(is);
        } catch (FileNotFoundException e) {
            throw new UtilException("File [" + fileName + "] not found", e);
        }
    }

    public static OutputStream openOutputStream(String fileName) throws UtilException {
        try {
            File file = new File(fileName);
            OutputStream os = new FileOutputStream(file);
            return new BufferedOutputStream(os);
        } catch (FileNotFoundException e) {
            throw new UtilException("File [" + fileName + "] not found", e);
        }
    }

    /**
     * 作为字节流读取
     */
    public static byte[] readInputStreamAsBytes(InputStream is) throws UtilException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int count, offset = 0;
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            while ((count = bis.read(buf)) > 0) {
                bos.write(buf, offset, 1024);
                offset += count;
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new UtilException("io failed.cause:" + e.getMessage(), e);
        }
    }

    /**
     * 作为字符流读取
     */
    public static String readInputStream(InputStream is) throws UtilException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line)
                        .append("\n");
            }
            // 如果文件非空，去掉末尾的"\n"
            return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
        } catch (IOException e) {
            throw new UtilException("io failed.cause:" + e.getMessage(), e);
        }
    }

    public static byte[] readFileAsBytes(File file) throws UtilException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return readInputStreamAsBytes(is);
        } catch (FileNotFoundException e) {
            throw new UtilException("File not found", e);
        } catch (IOException e) {
            throw new UtilException("io failed.cause:" + e.getMessage(), e);
        }
    }

    public static String readFile(File file) throws UtilException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return readInputStream(is);
        } catch (FileNotFoundException e) {
            throw new UtilException("File not found", e);
        } catch (IOException e) {
            throw new UtilException("io failed.cause:" + e.getMessage(), e);
        }
    }

    public static void writeFileAsBytes(File file, byte[] bytes) throws UtilException {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            os.write(bytes);
        } catch (FileNotFoundException e) {
            throw new UtilException("File not found", e);
        } catch (IOException e) {
            throw new UtilException("io failed.cause:" + e.getMessage(), e);
        }
    }

    public static void writeFile(File file, String data) throws UtilException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file))))) {
            writer.write(data);
        } catch (FileNotFoundException e) {
            throw new UtilException("File not found", e);
        } catch (IOException e) {
            throw new UtilException("io failed.cause:" + e.getMessage(), e);
        }
    }

    public static void writeStream(File targetFile, InputStream in) throws UtilException {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            try (BufferedInputStream bin = new BufferedInputStream(in)) {
                int len;
                byte[] buf = new byte[1024];
                while ((len = bin.read(buf)) != -1) {
                    out.write(buf, 0, len);
                    out.flush();
                }
            } catch (FileNotFoundException e) {
                throw new UtilException("File not found", e);
            } catch (IOException e) {
                throw new UtilException("io failed.cause:" + e.getMessage(), e);
            }
        } catch (FileNotFoundException e) {
            throw new UtilException("File not found", e);
        } catch (IOException e) {
            throw new UtilException("io failed.cause:" + e.getMessage(), e);
        }
    }

    ///////////////////////////////// 读取 /////////////////////////////////

    /**
     * 从文件系统根路径读
     */
    public static byte[] readFileFromDiskAsBytes(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("从文件系统读取的文件路径必须以'/'开头");
        }
        File file = new File(DISK_PATH + fileName);
        return readFileAsBytes(file);
    }

    /**
     * 从项目根路径读
     */
    public static byte[] readFileFromProjectAsBytes(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("从项目读取的文件的路径必须以'/'开头");
        }
        File file = new File(PROJECT_PATH + fileName);
        return readFileAsBytes(file);
    }

    /**
     * 从ClassPath读
     */
    public static byte[] readFileFromClassPathAsBytes(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("从classPath读取的文件的路径必须以'/'开头");
        }
        File file = new File(CLASS_PATH + fileName);
        return readFileAsBytes(file);
    }

    /**
     * 从文件系统根路径读
     */
    public static String readFileFromDisk(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("从文件系统读取的文件路径必须以'/'开头");
        }
        File file = new File(DISK_PATH + fileName);
        return readFile(file);
    }

    /**
     * 从项目根路径读
     */
    public static String readFileFromProject(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("从项目读取的文件的路径必须以'/'开头");
        }
        File file = new File(PROJECT_PATH + fileName);
        return readFile(file);
    }

    /**
     * 从ClassPath读
     */
    public static String readFileFromClassPath(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("从classPath读取的文件的路径必须以'/'开头");
        }
        File file = new File(CLASS_PATH + fileName);
        return readFile(file);
    }

    public static void writeFileToDisk(String fileName, byte[] data) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("写入文件系统的文件路径必须以'/'开头");
        }
        File file = new File(DISK_PATH + fileName);
        // 考虑原文件已存在的情况，需要备份
        if (file.exists()) {
            backup(file);
        }
        touch(file);
        writeFileAsBytes(file, data);
    }

    ///////////////////////////////// 写文件，入参为文件内容 /////////////////////////////////
    public static void writeFileToDisk(String fileName, String data) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("写入文件系统的文件路径必须以'/'开头");
        }
        File file = new File(DISK_PATH + fileName);
        // 考虑原文件已存在的情况，需要备份
        if (file.exists()) {
            backup(file);
        }
        touch(file);
        writeFile(file, data);
    }

    public static void writeFileToProject(String fileName, String data) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("写入项目的文件路径必须以'/'开头");
        }
        File file = new File(PROJECT_PATH + fileName);
        // 考虑原文件已存在的情况，需要备份
        if (file.exists()) {
            backup(file);
        }
        touch(file);
        writeFile(file, data);
    }

    public static void writeFileToClassPath(String fileName, String data) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("写入classPath的文件路径必须以'/'开头");
        }
        File file = new File(CLASS_PATH + fileName);
        // 考虑原文件已存在的情况，需要备份
        if (file.exists()) {
            backup(file);
        }
        touch(file);
        writeFile(file, data);
    }

    ///////////////////////////////// 写文件，入参为InputStream类型，主要用于上传 /////////////////////////////////
    public static void writeFileToDisk(String fileName, InputStream in) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("写入文件系统的文件路径必须以'/'开头");
        }
        File targetFile = new File(DISK_PATH + fileName);
        // 考虑原文件已存在的情况，需要备份
        if (targetFile.exists()) {
            backup(targetFile);
        }
        touch(targetFile);
        writeStream(targetFile, in);
    }

    public static void writeFileToProject(String fileName, InputStream in) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("写入项目的文件路径必须以'/'开头");
        }
        File file = new File(PROJECT_PATH + fileName);
        // 考虑原文件已存在的情况，需要备份
        if (file.exists()) {
            backup(file);
        }
        touch(file);
        writeStream(file, in);
    }

    public static void writeFileToClassPath(String fileName, InputStream in) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("写入classPath的文件路径必须以'/'开头");
        }
        File file = new File(CLASS_PATH + fileName);
        // 考虑原文件已存在的情况，需要备份
        if (file.exists()) {
            backup(file);
        }
        touch(file);
        writeStream(file, in);
    }

    ///////////////////////////////// 删除文件 /////////////////////////////////
    public static void delFileFromDisk(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("删除文件系统的文件路径必须以'/'开头");
        }
        File file = new File(DISK_PATH + fileName);
        backup(file);
        if (!file.delete()) {
            throw new UtilException("Delete file [" + fileName + "] failed");
        }
    }

    public static void delFileFromProject(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("删除项目文件的文件路径必须以'/'开头");
        }
        File file = new File(PROJECT_PATH + fileName);
        backup(file);
        if (!file.delete()) {
            throw new UtilException("Delete file [" + fileName + "] failed");
        }
    }

    public static void delFileFromClassPath(String fileName) throws UtilException {
        if (!fileName.startsWith("/")) {
            throw new UtilException("删除classPath文件的文件路径必须以'/'开头");
        }
        File file = new File(CLASS_PATH + fileName);
        backup(file);
        if (!file.delete()) {
            throw new UtilException("Delete file [" + fileName + "] failed");
        }
    }

    //////////////// 其他操作 /////////////////

    /**
     * 从输入流中往输出流中拷贝数据
     *
     * @param in 输入流
     * @param out 输出流
     */
    public static void copy(InputStream in, OutputStream out) throws UtilException {
        BufferedInputStream bin = new BufferedInputStream(in);
        BufferedOutputStream bout = new BufferedOutputStream(out);
        byte[] row = new byte[4096];
        try {
            int count = bin.read(row);
            while (count > 0) {
                bout.write(row, 0, count);
                count = bin.read(row);
            }
            bout.flush();
        } catch (IOException ex) {
            throw new UtilException(ex);
        }
    }

    /**
     * 根据文件过滤器列出当前目录下的文件列表
     *
     * @param folder 文件目录
     * @param filter 文件过滤器
     * @return 文件列表
     */
    public static File[] listFile(File folder, FilenameFilter filter) {
        File[] files = folder.listFiles(filter);
        if (files == null) {
            files = new File[0];
        }
        return files;
    }

    public static File[] listFileRecursively(File folder, FilenameFilter filter) {
        List<File> fileList = new ArrayList<>();
        File[] curLevelFiles = folder.listFiles(filter);
        if (null == curLevelFiles) {
            return new File[0];
        }
        fileList.addAll(Arrays.asList(curLevelFiles));
        for (File file : curLevelFiles) {
            if (file.isDirectory()) {
                fileList.addAll(Arrays.asList(listFileRecursively(file, filter)));
            }
        }
        return fileList.toArray(new File[0]);
    }

    public static File[] listFiles(String path) {
        return listFiles(path, (dir, name) -> true);
    }

    public static File[] listFiles(String path, FilenameFilter filter) {
        File file = new File(path);
        if (!file.exists()) {
            return new File[0];
        }
        if (file.isFile()) {
            return filter.accept(null, path)
                    ? new File[]{file}
                    : new File[0];
        }
        return listFileRecursively(file, filter);
    }

    /**
     * 列出当前目录下的子文件目录
     *
     * @param folder 当前目录
     * @return 子文件目录
     */
    public static File[] listFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return new File[0];
        }
        List<File> list = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory()) {
                list.add(file);
            }
        }
        int size = list.size();
        File[] folders = new File[size];
        folders = list.toArray(folders);
        return folders;
    }

}
