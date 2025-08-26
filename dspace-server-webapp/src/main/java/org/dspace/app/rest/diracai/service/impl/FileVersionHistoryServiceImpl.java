package org.dspace.app.rest.diracai.service.impl;

import org.dspace.content.Diracai.FileVersionHistory;
import org.dspace.app.rest.diracai.Repository.FileVersionHistoryRepository;
import org.dspace.app.rest.diracai.service.FileVersionHistoryService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FileVersionHistoryServiceImpl implements FileVersionHistoryService {

    @Autowired
    private FileVersionHistoryRepository versionHistoryRepository;

    @Override
    public void saveVersion(Context context, String fileName, String versionId, byte[] fileContent, String comment) {
        FileVersionHistory history = new FileVersionHistory();
        history.setFileName(fileName);
        history.setVersionId(versionId);
        history.setFileContent(fileContent);
        history.setModifiedBy(context.getCurrentUser() != null ? context.getCurrentUser().getEmail() : "anonymous");
        history.setComment(comment);
        history.setModifiedAt(new Date());

        versionHistoryRepository.save(history);
    }

    @Override
    public FileVersionHistory getLatestVersion(String fileName) {
        List<FileVersionHistory> all = versionHistoryRepository.findAll();
        return all.stream()
                .filter(f -> f.getFileName().equals(fileName))
                .reduce((first, second) -> second) // last one
                .orElse(null);
    }
}
