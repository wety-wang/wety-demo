package com.wety.demo.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 文件工具类
 *
 * @author wety
 */
public class FileUtils {

    private FileUtils() {
    }

    /**
     * 校验目录是否存在，不存在就创建。
     *
     * @param destDir 要校验的目录
     *
     * @return 创建失败就返回false。存在或创建成功就返回true
     */
    public static boolean checkDirectoryExist(String destDir) {
        File directory = new File(destDir);
        if (directory.exists() && directory.isDirectory()) {
            return true;
        }
        return directory.mkdirs();
    }

    /**
     * 获取文件后缀名（不带点）.
     *
     * @return 如："jpg" or "".
     */
    public static String getFileExt(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return "";
        } else {
            // 不带最后的点
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
    }

    /**
     * 删除目录，如果目录下有子目录或文件，一并删除
     *
     * @param targetDir
     * @throws Exception
     */
    public static void removeDir(String targetDir) throws Exception {
        File dir = new File(targetDir);
        org.apache.commons.io.FileUtils.forceDelete(dir);
    }
}
