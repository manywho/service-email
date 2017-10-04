package com.manywho.services.email.email.attachments;

import com.google.common.base.Strings;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.ApplicationConfiguration;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;

import javax.inject.Inject;
import java.io.IOException;

public class EmailAttachmentManager {
    private final BoxSource boxSource;
    private final DefaultSource defaultSource;
    private final S3Source s3Source;
    private final Tika tika;

    @Inject
    public EmailAttachmentManager(BoxSource boxSource, DefaultSource defaultSource, S3Source s3Source, Tika tika) {
        this.boxSource = boxSource;
        this.defaultSource = defaultSource;
        this.s3Source = s3Source;
        this.tika = tika;
    }

    public SourceFile fetchAttachment(ApplicationConfiguration configuration, $File file) {
        SourceFile sourceFile;

        switch (configuration.getAttachmentSource()) {
            case Box:
                sourceFile = boxSource.download(configuration, file.getId());
                break;
            case S3:
                sourceFile = s3Source.download(configuration, file.getId());
                break;
            default:
                sourceFile = defaultSource.download(configuration, file.getId());
                break;
        }

        String mimeType = file.getMimeType();

        // If we're not given a mime type in the incoming $File object, we try and detect one
        if (Strings.isNullOrEmpty(mimeType)) {
            try (TikaInputStream tikaInputStream = TikaInputStream.get(sourceFile.getFileData())) {
                mimeType = tika.detect(tikaInputStream);
            } catch (IOException e) {
                throw new RuntimeException("Unable to generate a source for the attachment: " + file.getName(), e);
            }
        }

        sourceFile.setMimeType(mimeType);

        return sourceFile;
    }
}