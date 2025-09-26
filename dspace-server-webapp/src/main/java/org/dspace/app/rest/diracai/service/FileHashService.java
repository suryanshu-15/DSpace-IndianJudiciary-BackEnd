package org.dspace.app.rest.diracai.service;

import org.dspace.content.Diracai.FileHashRecord;
import org.dspace.core.Context;

import java.io.IOException;

public interface FileHashService {
    FileHashRecord generateZipAndHash(String cnr, Context context , String docType) throws IOException;
    enum DeleteResult { DELETED, NOT_FOUND }

    /** Delete zip from disk and remove the corresponding DB record if present. */
    DeleteResult deleteZipAndRecord(String fileName) throws IOException;
}
