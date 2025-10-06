package org.dspace.app.rest.diracai.service.impl;



import org.dspace.app.rest.diracai.Repository.BulkUploadItemRepository;
import org.dspace.app.rest.diracai.service.BulkUploadItemService;
import org.dspace.content.Diracai.BulkUploadItem;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class BulkUploadItemServiceImpl implements BulkUploadItemService {

    @Autowired
    private BulkUploadItemRepository itemRepository;

    @Override
    @Transactional
    public BulkUploadItem create(Context context, BulkUploadItem item) {
        return itemRepository.save(item);
    }
}
