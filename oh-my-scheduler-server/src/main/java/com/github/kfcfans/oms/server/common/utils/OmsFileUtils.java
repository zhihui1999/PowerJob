package com.github.kfcfans.oms.server.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 文件工具类，统一文件存放地址
 *
 * @author tjq
 * @since 2020/5/15
 */
public class OmsFileUtils {

    private static final String USER_HOME = System.getProperty("user.home", "oms");
    private static final String COMMON_PATH = USER_HOME + "/oms-server/";

    /**
     * 获取在线日志的存放路径
     * @return 在线日志的存放路径
     */
    public static String genLogDirPath() {
        return COMMON_PATH + "online_log/";
    }

    /**
     * 获取用于构建容器的 jar 文件存放路径
     * @return 路径
     */
    public static String genContainerJarPath() {
        return COMMON_PATH + "container/jar/";
    }

    /**
     * 获取临时目录，用完记得删除
     * @return 临时目录
     */
    public static String genTemporaryPath() {
        String uuid = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        return COMMON_PATH + "temporary/" + uuid + "/";
    }

    /**
     * 将文本写入文件
     * @param content 文本内容
     * @param file 文件
     */
    public static void string2File(String content, File file) {
        try(FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }catch (IOException ie) {
            ExceptionUtils.rethrow(ie);
        }
    }

    /**
     * 输出文件（对外下载功能）
     * @param file 文件
     * @param response HTTP响应
     * @throws IOException 异常
     */
    public static void file2HttpResponse(File file, HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));

        byte[] buffer = new byte[4096];
        try (BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {

            while (bis.read(buffer) != -1) {
                bos.write(buffer);
            }
        }
    }

    /**
     * 将 mongoDB 中的数据转存到本地文件中
     * @param gridFsResource mongoDB 文件资源
     * @param targetFile 本地文件资源
     */
    public static void gridFs2File(GridFsResource gridFsResource, File targetFile) {

        byte[] buffer = new byte[1024];
        try (BufferedInputStream gis = new BufferedInputStream(gridFsResource.getInputStream());
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile))
        ) {
            while (gis.read(buffer) != -1) {
                bos.write(buffer);
            }
            bos.flush();
        }catch (IOException ie) {
            ExceptionUtils.rethrow(ie);
        }
    }
}