package org.dspace.app.rest.diracai.Repository;

import org.dspace.content.Diracai.FileAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileAccessLogRepository extends JpaRepository<FileAccessLog, UUID> {
    List<FileAccessLog> findByUserId(UUID userId);
    List<FileAccessLog> findByFileId(UUID fileId);
}
