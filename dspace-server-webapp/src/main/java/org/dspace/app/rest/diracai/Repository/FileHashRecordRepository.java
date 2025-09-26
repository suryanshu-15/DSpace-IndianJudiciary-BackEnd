package org.dspace.app.rest.diracai.Repository;

import org.dspace.content.Diracai.FileHashRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FileHashRecordRepository extends JpaRepository<FileHashRecord, Long> {
    FileHashRecord findByFileName(String cnr);
    FileHashRecord findByAckId(String ackId);
    List<FileHashRecord> findAll(Sort sort);
    @Query("""
        select f from FileHashRecord f
        where f.createdAt between :from and :to
        order by f.createdAt desc
    """)
    List<FileHashRecord> findAllForReport(@Param("from") LocalDateTime from,
                                          @Param("to") LocalDateTime to);
    Page<FileHashRecord> findByAckIdIsNotNullAndAckIdNot(Pageable pageable, String emptyValue);
    Page<FileHashRecord> findByAckIdIsNullOrAckId(Pageable pageable, String emptyValue);
}
