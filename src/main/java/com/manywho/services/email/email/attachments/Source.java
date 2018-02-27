package com.manywho.services.email.email.attachments;

import com.manywho.services.email.ApplicationConfiguration;

public interface Source {
    /**
     * Download the file from the source and return a wrapper for it, along with some required metadata.
     * <p>
     * This method should also check the file size against the limit (preferably before downloading) to avoid any
     * potential DOS attacks, and to fit with most email provider limits.
     *
     * @param configuration the configuration given by the Flow
     * @param key           the key of the file inside the source
     * @return              a wrapper object containing the file data and metadata
     */
    SourceFile download(ApplicationConfiguration configuration, String key);
}