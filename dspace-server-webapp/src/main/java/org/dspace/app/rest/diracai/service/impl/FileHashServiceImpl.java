package org.dspace.app.rest.diracai.service.impl;

import org.dspace.app.rest.diracai.Repository.FileHashRecordRepository;
import org.dspace.app.rest.diracai.util.ZipGenerationUtil;
import org.dspace.app.rest.diracai.service.FileHashService;
import org.dspace.content.Diracai.FileHashRecord;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

@Service
public class FileHashServiceImpl implements FileHashService {

    @Autowired
    private FileHashRecordRepository repository;

    @Autowired
    private ZipGenerationUtil zipGenerationUtil;

//    @Override
//    public FileHashRecord generateZipAndHash(Context context) throws IOException {
//        context.turnOffAuthorisationSystem();
//        File outputZip = zipGenerationUtil.generateZipWithFiles();  // creates {CNR}.zip
//        String sha256 = zipGenerationUtil.computeSHA256(outputZip);
//        FileHashRecord record = new FileHashRecord(outputZip.getName(), sha256);
//        context.restoreAuthSystemState();
//        return repository.save(record);
//    }


    @Override
    public FileHashRecord generateZipAndHash(String cnr, Context context , String docType) throws IOException {
        context.turnOffAuthorisationSystem();

        FileHashRecord record = new FileHashRecord();
        File outputZip = null;

        try {
            outputZip = zipGenerationUtil.generateZipWithFiles(cnr,docType); // may throw IOException
            record.setZipStatus("ZIP file created successfully");

            String sha256 = zipGenerationUtil.computeSHA256(outputZip);
            record.setFileName(outputZip.getName());
            record.setHashValue(sha256);

        } catch (Exception e) {
            record.setZipStatus("Failed to create ZIP file: " + e.getMessage());
        }

        try {
            Path zipPath = outputZip.toPath();
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_READ);
            perms.add(PosixFilePermission.GROUP_WRITE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_READ);
            perms.add(PosixFilePermission.OTHERS_WRITE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(zipPath, perms);
        } catch (UnsupportedOperationException e) {
            record.setZipStatus("ZIP created but permission setting skipped (not POSIX)");
        }

        context.restoreAuthSystemState();
        return repository.save(record);
    }

}
