package com.zqw.fileoperation.functions;

import android.content.Context;

import com.zqw.fileoperation.pojos.MyFile;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 51376 on 2018/3/16.
 */

public class FileFounder {
    private FileFounder() {
    }

    public static List<MyFile> getFilesFromDir(String path, Context context) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            // Toast.makeText(context, "路径不存在或已经被删除！", Toast.LENGTH_SHORT).show();
            return null;
        }
//        //test
//        if(file.getName().contains("data")){
//            return null;
//        }
//        //test
        List<MyFile> myFiles = new LinkedList<>();
        File[] subFiles = file.listFiles();
        for (File subFile : subFiles) {
            MyFile myFile = new MyFile();
            String subFileName = subFile.getName();
            if (subFileName.length() > 30) {
                subFileName = subFileName.substring(0, 30);
                subFileName = subFileName + "...";
            }
            myFile.setFileName(subFileName);
            myFile.setAbsolutePath(subFile.getAbsolutePath());
            if (subFile.isDirectory()) {
                myFile.setFileDescribe("目录");
                myFile.setType(MyFile.TYPE_DIR);
            } else {
                myFile.setFileDescribe("文件");
                myFile.setType(MyFile.TYPE_FILE);
            }
            myFiles.add(myFile);
        }
        return myFiles;
    }
}
