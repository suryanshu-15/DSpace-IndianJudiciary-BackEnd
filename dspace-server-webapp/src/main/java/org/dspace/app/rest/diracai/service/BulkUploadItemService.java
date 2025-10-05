package org.dspace.app.rest.diracai.service;

import org.dspace.content.Diracai.BulkUploadItem;
import org.dspace.core.Context;

public interface BulkUploadItemService {
    BulkUploadItem create(Context context, BulkUploadItem item);
}