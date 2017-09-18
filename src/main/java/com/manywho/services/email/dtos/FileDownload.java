package com.manywho.services.email.dtos;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDownload {

    private String fileName;
    private byte[] fileInput;
    private String mimeType;

    public FileDownload(InputStream fileInput, String name) throws IOException {
        this.fileInput = IOUtils.toByteArray(fileInput);
        this.fileName = name;
        this.mimeType = getMimeType(this.fileInput);
    }

    public FileDownload(byte[] fileInput, String name) throws IOException {
        this.fileInput = fileInput;
        this.fileName = name;
        this.mimeType = getMimeType(this.fileInput);
    }

    private String getMimeType(final byte[] fileInput) {
        final Tika tika = new Tika();
        try (TikaInputStream tikaInputStream = TikaInputStream.get(fileInput)){
            return tika.detect(tikaInputStream);
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
    public InputStream getFileInput() {
        return new ByteArrayInputStream(fileInput);
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("File Name", fileName)
                .append("Mime type", mimeType).toString();
    }

}
