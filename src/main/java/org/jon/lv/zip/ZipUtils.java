package org.jon.lv.zip;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.jon.lv.files.FolderUtils;

import java.io.*;
import java.util.Enumeration;

/**
 * @Package org.jon.lv.zip.ZipUtils
 * @Description: 压缩工具类
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/6/15 15:12
 * version V1.0.0
 */
public class ZipUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 压缩文件夹
     *
     * @param zipFileName
     *            打包后文件的名称，含路径
     * @param sourceFolder
     *            需要打包的文件夹或者文件的路径
     * @param zipPathName
     *            打包目的文件夹名,为空则表示直接打包到根
     */
    public static void zip(String zipFileName, String sourceFolder, String zipPathName) throws Exception {
        ZipOutputStream out = null;
        try {
            File zipFile = new File(zipFileName);

            FolderUtils.mkdirs(zipFile.getParent());
            out = new ZipOutputStream(zipFile);
            out.setEncoding(DEFAULT_CHARSET);
            if (StringUtils.isNotBlank(zipPathName)) {
                zipPathName = FilenameUtils.normalizeNoEndSeparator(zipPathName, true) + "/";
            } else {
                zipPathName = "";
            }
            zip(out, sourceFolder, zipPathName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 压缩文件夹
     *
     * @param zipFile
     *            a {@link java.lang.String} object.
     * @param source
     *            a {@link java.lang.String} object.
     */
    public static void zip(String zipFile, String source) throws Exception {
        File file = new File(source);
        zip(zipFile, source, file.isFile() ? StringUtils.EMPTY : file.getName());
    }

    /**
     * 压缩文件夹
     *
     * @param zipFile
     *            a {@link java.io.File} object.
     * @param source
     *            a {@link java.io.File} object.
     */
    public static void zip(File zipFile, File source) throws Exception {
        zip(zipFile.getAbsolutePath(), source.getAbsolutePath());
    }

    private static void zip(ZipOutputStream zos, String file, String pathName) throws IOException {
        File file2zip = new File(file);
        if (file2zip.isFile()) {
            zos.putNextEntry(new ZipEntry(pathName + file2zip.getName()));
            IOUtils.copy(new FileInputStream(file2zip.getAbsolutePath()), zos);
            zos.flush();
            zos.closeEntry();
        } else {
            File[] files = file2zip.listFiles();
            if (ArrayUtils.isNotEmpty(files)) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        zip(zos, FilenameUtils.normalizeNoEndSeparator(f.getAbsolutePath(), true),
                                FilenameUtils.normalizeNoEndSeparator(pathName + f.getName(), true) + "/");
                    } else {
                        zos.putNextEntry(new ZipEntry(pathName + f.getName()));
                        IOUtils.copy(new FileInputStream(f.getAbsolutePath()), zos);
                        zos.flush();
                        zos.closeEntry();
                    }
                }
            }
        }
    }

    /**
     * 解压
     *
     * @param fromZipFile
     *            zip文件路径
     * @param unzipPath
     *            解压路径
     */
    @SuppressWarnings("unchecked")
    public static final void unzip(String fromZipFile, String unzipPath) throws Exception {

        FileOutputStream fos = null;
        InputStream is = null;
        String path1 = StringUtils.EMPTY;
        String tempPath = StringUtils.EMPTY;

        if (!new File(unzipPath).exists()) {
            new File(unzipPath).mkdir();
        }
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(fromZipFile, DEFAULT_CHARSET);
        } catch (IOException e1) {
            e1.printStackTrace();
            throw new Exception(e1);
        }
        File temp = new File(unzipPath);
        String strPath = temp.getAbsolutePath();
        Enumeration<ZipEntry> enu = zipFile.getEntries();
        ZipEntry zipEntry = null;
        while (enu.hasMoreElements()) {
            zipEntry = (ZipEntry) enu.nextElement();
            path1 = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                tempPath = FilenameUtils.normalizeNoEndSeparator(strPath + File.separator + path1, true);
                File dir = new File(tempPath);
                dir.mkdirs();
                continue;
            } else {

                BufferedInputStream bis = null;
                BufferedOutputStream bos = null;
                try {
                    is = zipFile.getInputStream(zipEntry);
                    bis = new BufferedInputStream(is);
                    path1 = zipEntry.getName();
                    tempPath = FilenameUtils.normalizeNoEndSeparator(strPath + File.separator + path1, true);
                    FolderUtils.mkdirs(new File(tempPath).getParent());
                    fos = new FileOutputStream(tempPath);
                    bos = new BufferedOutputStream(fos);

                    IOUtils.copy(bis, bos);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new Exception(e);
                } finally {
                    IOUtils.closeQuietly(bis);
                    IOUtils.closeQuietly(bos);
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(fos);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        zip("d:/home/test.zip", "d:/dtd", "");
    }
}
