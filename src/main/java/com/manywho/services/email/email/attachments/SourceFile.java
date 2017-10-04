package com.manywho.services.email.email.attachments;

public class SourceFile {
    private final byte[] fileData;
    private final String name;
    private String mimeType;

    public SourceFile(byte[] fileData, String name) {
        this.fileData = fileData;
        this.name = name;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}