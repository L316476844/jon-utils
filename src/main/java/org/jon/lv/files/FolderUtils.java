package org.jon.lv.files;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @Package org.jon.lv.files.FolderUtils
 * @Description: 文件夹工具
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/6/15 14:59
 * version V1.0.0
 */
public class FolderUtils {

    /**
     * 创建完整路径
     *
     * @param path
     *            a {@link java.lang.String} object.
     */
    public static final void mkdirs(final String... path) {
        for (String foo : path) {
            final String realPath = FilenameUtils.normalizeNoEndSeparator(foo, true);
            final File folder = new File(realPath);
            if (!folder.exists() || folder.isFile()) {
                folder.mkdirs();
            }
        }
    }

}