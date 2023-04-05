package com.ks.sd.util.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

public class SdFileUtil {
    /**
     * 폴더 생성
     * @param path
     * @param dirName
     * @return boolean
     */
    public static boolean mkdirs(String path) {
        boolean isResult = false;
        Path fullPath = Paths.get(path);
        isResult = fullPath.toFile().mkdirs();

        return isResult;
    }

    /**
     * 폴더 및 폴더 내 모든 파일 삭제 함수
     * @param path
     * @return boolean
     */
    public static boolean deleteDirectory(String path) {
        boolean isResult = false;
        Path fullPath = Paths.get(path, File.separator);

        try {
            FileUtils.deleteDirectory(fullPath.toFile());
            isResult = true;
        } catch (Exception e) {
            isResult = false;
        }

        return isResult;
    }

    /**
     * 폴더 내 모든 파일 및 폴더 삭제 함수
     * @param path
     * @return boolean
     */
    public static boolean cleanDirectory(String path) {
        boolean isResult = false;
        Path fullPath = Paths.get(path, File.separator);

        try {
            FileUtils.cleanDirectory(fullPath.toFile());
            isResult = true;
        } catch (Exception e) {
            isResult = false;
        }

        return isResult;
    }

    /**
     * 파일 삭제
     * @param path
     * @return boolean
     */
    public static boolean deleteFile(String path) {
        boolean isResult = false;
        Path fullPath = Paths.get(path);
        isResult = fullPath.toFile().delete();

        return isResult;
    }
}
