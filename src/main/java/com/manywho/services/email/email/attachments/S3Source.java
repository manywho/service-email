package com.manywho.services.email.email.attachments;

import com.manywho.services.email.ApplicationConfiguration;

public class S3Source implements Source {

    @Override
    public SourceFile download(ApplicationConfiguration configuration, String key) {
        return null;
    }
}