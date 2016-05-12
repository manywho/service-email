package com.manywho.services.email.service;

import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class FileService {

    public BodyPart getFilePart(FormDataMultiPart formDataMultiPart) throws Exception {
        // If the filename is blank or doesn't exist, assume it's the FileDataRequest and skip it
        Optional<BodyPart> filePart = formDataMultiPart.getBodyParts().stream()
                .filter(bodyPart -> StringUtils.isNotEmpty(bodyPart.getContentDisposition().getFileName()))
                .findFirst();

        if (filePart.isPresent()) {
            return filePart.get();
        }

        throw new Exception("A file could not be found in the received request");
    }


    public Object getObjectFile(FormDataMultiPart formDataMultiPart, BodyPart bodyPart, UUID id) {
        Object object = new Object();

        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Kind", bodyPart.getMediaType()));
        properties.add(new Property("ID", id.toString()));
        properties.add(new Property("Mime Type", bodyPart.getMediaType()));
        properties.add(new Property("Name", formDataMultiPart.getField("file").getFormDataContentDisposition().getFileName()));
        properties.add(new Property("Description", "attached file"));
        properties.add(new Property("Date Created", new Date()));
        properties.add(new Property("Date Modified", new Date()));
        properties.add(new Property("Download Uri", "https://manywho.com/files/upload/" + id.toString()));
        properties.add(new Property("Embed Uri"));
        properties.add(new Property("Icon Uri"));
        object.setDeveloperName("$File");
        object.setExternalId(id.toString());
        object.setProperties(properties);
        return object;
    }
}
