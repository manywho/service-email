package com.manywho.services.email.managers;

import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.handler.BoxFileHandler;
import com.manywho.services.email.handler.FileHandler;
import com.manywho.services.email.handler.S3FileHandler;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class FileManager {

    private BoxFileHandler boxFileHandler;
    private S3FileHandler s3FileHandler;

    @Inject
    public FileManager(BoxFileHandler boxFileHandler, S3FileHandler s3FileHandler) throws IOException {
        this.boxFileHandler = boxFileHandler;
        this.s3FileHandler = s3FileHandler;
    }

    public FileDownload downloadFile(Configuration configuration, String fileId) throws IOException {
        return getRequestFileHandler(configuration).downloadFile(configuration, fileId);
    }

    public ObjectDataResponse uploadFile(Configuration configuration, FormDataMultiPart formDataMultiPart) throws Exception {
        return getRequestFileHandler(configuration).uploadFile(configuration, formDataMultiPart);
    }

    public void deleteFiles(Configuration configuration, List<$File> files) throws IOException {
        getRequestFileHandler(configuration).deleteFiles(configuration, files);
    }

    private FileHandler getRequestFileHandler(Configuration configuration) {
        return configuration.getUseBoxForAttachment() ? boxFileHandler : s3FileHandler;
    }
}
