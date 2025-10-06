package org.dspace.app.rest.diracai.Repository;

import org.dspace.content.Diracai.FileVersionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileVersionHistoryRepository extends JpaRepository<FileVersionHistory, Long> {
}
