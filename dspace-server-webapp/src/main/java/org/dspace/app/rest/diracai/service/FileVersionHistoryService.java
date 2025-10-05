package org.dspace.app.rest.diracai.service;

import org.dspace.content.Diracai.FileVersionHistory;
import org.dspace.core.Context;

public interface FileVersionHistoryService {
    void saveVersion(Context context, String fileName, String versionId, byte[] fileContent, String comment);
    FileVersionHistory getLatestVersion(String fileName);
}
