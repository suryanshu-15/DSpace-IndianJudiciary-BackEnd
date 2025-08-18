package org.dspace.app.rest.diracai.service;

import org.dspace.content.Diracai.FileHashRecord;
import org.dspace.core.Context;

import java.io.IOException;

public interface FileHashService {
    FileHashRecord generateZipAndHash(String cnr, Context context , String docType) throws IOException;
}
