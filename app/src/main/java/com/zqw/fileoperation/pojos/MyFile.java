package com.zqw.fileoperation.pojos;

import java.io.Serializable;

/**
 * Created by 51376 on 2018/3/16.
 */

public class MyFile implements Serializable{
    private String fileName;
    private String fileDescribe;
    private int type;
    private String absolutePath;
    private long size;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDescribe() {
        return fileDescribe;
    }

    public void setFileDescribe(String fileDescribe) {
        this.fileDescribe = fileDescribe;
    }

    //绝对路径相同认为是同一个文件
    @Override
    public boolean equals(Object obj) {
        if (((MyFile) obj).getAbsolutePath().equals(absolutePath)) {
            return true;
        }
        return false;
    }
}
