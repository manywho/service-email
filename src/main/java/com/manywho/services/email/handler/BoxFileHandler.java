package com.manywho.services.email.handler;

import com.box.sdk.BoxFile;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.cache.ServiceCacheProvider;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import org.apache.commons.lang3.NotImplementedException;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class BoxFileHandler implements FileHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoxFileHandler.class);
    private ServiceCacheProvider serviceCacheProvider;

    @Inject
    public BoxFileHandler(ServiceCacheProvider serviceCacheProvider) {
        this.serviceCacheProvider = serviceCacheProvider;
    }

    @Override
    public FileDownload downloadFile(Configuration configuration, String fileId) throws IOException {
        BoxFile boxFile = getBoxFile(configuration, fileId);
        BoxFile.Info info = boxFile.getInfo();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            boxFile.download(outputStream);
            FileDownload fileDownload = new FileDownload(outputStream.toByteArray(), info.getName());
            LOGGER.info(String.format("File downloaded from box is %s",fileDownload));
            return fileDownload;
        }
    }

    @Override
    public ObjectDataResponse uploadFile(Configuration configuration, FormDataMultiPart formDataMultiPart) throws Exception {
        LOGGER.warn("upload file method not implemented for BoxFileHandler");
        throw new NotImplementedException("Upload file to Box is not available via email service");
    }

    @Override
    public void deleteFiles(Configuration configuration, List<$File> files) throws IOException {
        LOGGER.warn("deleteFiles method not implemented for BoxFileHandler");
        throw new NotImplementedException("Delete files for Box is not available via email service");
    }

    protected BoxFile getBoxFile(Configuration configuration, String fileId) throws IOException {
        return new BoxFile(serviceCacheProvider.getBoxApiConnection(configuration), fileId);
    }

}
