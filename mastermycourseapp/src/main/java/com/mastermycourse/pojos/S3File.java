package com.mastermycourse.pojos;

import java.util.Date;

/**
 * Author: James DeCarlo.
 *
 * Holds filename and size of files from aws s3 list files method
 */
public class S3File {

    private String fileName;
    private long fileSize;
    private Date lastModified;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
