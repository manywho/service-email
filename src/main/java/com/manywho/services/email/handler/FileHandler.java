package com.manywho.services.email.handler;

import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import java.io.IOException;
import java.util.List;

public interface FileHandler {
    FileDownload downloadFile(Configuration configuration, String fileId) throws IOException;
    ObjectDataResponse uploadFile(Configuration configuration, FormDataMultiPart formDataMultiPart) throws Exception;
    void deleteFiles(Configuration configuration, List<$File> files) throws IOException;
}
