package org.dspace.app.rest.diracai.Repository;

import org.dspace.content.Diracai.BitstreamComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BitstreamCommentRepository extends JpaRepository<BitstreamComment, Integer> {
    List<BitstreamComment> findByBitstreamIdAndIsDeletedFalse(UUID bitstreamId);
}
