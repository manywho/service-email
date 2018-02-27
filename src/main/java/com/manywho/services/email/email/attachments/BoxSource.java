package com.manywho.services.email.email.attachments;

import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.google.common.base.Strings;
import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.configuration.ServiceConfiguration;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BoxSource implements Source {
    private final ServiceConfiguration serviceConfiguration;

    @Inject
    public BoxSource(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public SourceFile download(ApplicationConfiguration configuration, String key) {
        if (Strings.isNullOrEmpty(configuration.getBoxEnterprise())) {
            throw new RuntimeException("The service must be configured with a Box Enterprise ID to use Box as the attachment source");
        }

        BoxDeveloperEditionAPIConnection apiConnection = createConnection(configuration.getBoxEnterprise());

        BoxFile file = new BoxFile(apiConnection, key);
        BoxFile.Info fileInfo = file.getInfo();

        if (file.getInfo().getSize() > SourceConstants.SIZE_LIMIT) {
            throw new RuntimeException(String.format("The file with ID %s is larger than %s, which is the maximum supported attachment size", key, SourceConstants.SIZE_LIMIT_HUMAN));
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Download the file from Box, into the created output stream
            file.download(outputStream);

            return new SourceFile(outputStream.toByteArray(), fileInfo.getName());
        } catch (IOException e) {
            throw new RuntimeException("Unable to close the attachment stream", e);
        }
    }

    private BoxDeveloperEditionAPIConnection createConnection(String enterprise) {
        return BoxDeveloperEditionAPIConnection.getAppUserConnection(serviceConfiguration.get("box.appUserId"), new BoxConfig(
                serviceConfiguration.get("box.clientId"),
                serviceConfiguration.get("box.clientSecret"),
                enterprise,
                serviceConfiguration.get("box.publicKeyId"),
                serviceConfiguration.get("box.privateKey"),
                serviceConfiguration.get("box.privateKeyPassword")
        ));
    }
}