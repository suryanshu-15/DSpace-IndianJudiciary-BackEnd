package org.dspace.app.rest.diracai.Repository;

import org.dspace.content.Diracai.FileHashRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileHashRecordRepository extends JpaRepository<FileHashRecord, Long> {
    FileHashRecord findByFileName(String cnr);
    FileHashRecord findByAckId(String ackId);
    List<FileHashRecord> findAll(Sort sort);
}
