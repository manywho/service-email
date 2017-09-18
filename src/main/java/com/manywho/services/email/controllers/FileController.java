package com.manywho.services.email.controllers;

import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.managers.FileManager;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/file")
@Consumes("application/json")
@Produces("application/json")
public class FileController extends AbstractController {
    private FileManager fileManager;

    @Inject
    public FileController(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Path("/")
    @POST
    public ObjectDataResponse loadFiles(FileDataRequest fileDataRequest) throws Exception {
        ObjectCollection files = new ObjectCollection();
        return new ObjectDataResponse(files);
    }

    @POST
    @Path("/content")
    @Consumes({"multipart/form-data", "application/octet-stream"})
    public ObjectDataResponse uploadFile(@FormDataParam("FileDataRequest") FileDataRequest fileDataRequest, FormDataMultiPart file) throws Exception {
        Configuration configuration = this.parseConfigurationValues(fileDataRequest, Configuration.class);
        return fileManager.uploadFile(configuration, file);
    }
}
