//package org.dspace.app.rest.diracai.service.impl;
//
//import org.dspace.app.rest.diracai.Dto.Request.BitstreamCommentRequest;
//import org.dspace.app.rest.diracai.Dto.Response.BitstreamCommentResponse;
//import org.dspace.app.rest.diracai.Repository.BitstreamCommentRepository;
//import org.dspace.app.rest.diracai.service.BitstreamCommentService;
//import org.dspace.content.Diracai.BitstreamComment;
//import org.dspace.core.Context;
//import org.dspace.eperson.EPerson;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//import org.dspace.authorize.service.AuthorizeService;
//
//
//
//@Service
//public class BitstreamCommentServiceImpl implements BitstreamCommentService {
//
//    @Autowired
//    private BitstreamCommentRepository repository;
//    @Autowired
//    private AuthorizeService authorizeService;
//    @Override
//    public BitstreamCommentResponse create(Context context, BitstreamCommentRequest request) {
//        EPerson user = context.getCurrentUser();
//
//        // Check if the current user is an admin
//        try {
//            if (!authorizeService.isAdmin(context)) {
//                throw new RuntimeException("Only admin users are allowed to create comments.");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Proceed with creating the comment
//        BitstreamComment comment = new BitstreamComment(
//                request.getComment(),
//                request.getBitstreamId(),
//                user.getID()
//        );
//
//        BitstreamComment saved = repository.save(comment);
//        return new BitstreamCommentResponse(saved.getId(), saved.getText(), saved.getBitstreamId());
//    }
//
//
//    @Override
//    public BitstreamCommentResponse update(Context context, int id, BitstreamCommentRequest request) {
//        BitstreamComment existing = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Comment not found"));
//        existing.setText(request.getComment());
//        existing.setBitstreamId(request.getBitstreamId());
//        BitstreamComment saved = repository.save(existing);
//        return new BitstreamCommentResponse(saved.getId(), saved.getText(), saved.getBitstreamId());
//    }
//
//    @Override
//    public void delete(int id) {
//        BitstreamComment comment = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
//        comment.setDeleted(true);
//        repository.save(comment);
//    }
//
//
//    @Override
//    public BitstreamCommentResponse getById(int id) {
//        BitstreamComment comment = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Comment not found"));
//        return new BitstreamCommentResponse(comment.getId(), comment.getText(), comment.getBitstreamId());
//    }
//
//    @Override
//    public List<BitstreamCommentResponse> getByBitstreamId(UUID bitstreamId) {
//        return repository.findByBitstreamId(bitstreamId)
//                .stream()
//                .map(c -> new BitstreamCommentResponse(c.getId(), c.getText(), c.getBitstreamId()))
//                .collect(Collectors.toList());
//    }
//}


package org.dspace.app.rest.diracai.service.impl;

import org.dspace.app.rest.diracai.dto.Request.BitstreamCommentRequest;
import org.dspace.app.rest.diracai.dto.Response.BitstreamCommentResponse;
import org.dspace.app.rest.diracai.Repository.BitstreamCommentRepository;
import org.dspace.app.rest.diracai.service.BitstreamCommentService;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Diracai.BitstreamComment;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.EPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BitstreamCommentServiceImpl implements BitstreamCommentService {

    @Autowired
    private BitstreamCommentRepository repository;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private EPersonService ePersonService;


    @Override
    public BitstreamCommentResponse create(Context context, BitstreamCommentRequest request) {
        EPerson user = context.getCurrentUser();

        try {
            if (!authorizeService.isAdmin(context,user)) {
                throw new RuntimeException("Only admin users are allowed to create comments.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Authorization check failed", e);
        }

        BitstreamComment comment = new BitstreamComment(
                request.getComment(),
                request.getBitstreamId(),
                user.getID()
        );

        BitstreamComment saved = repository.save(comment);

        return mapToResponse(context , saved);
    }

    @Override
    public BitstreamCommentResponse update(Context context, int id, BitstreamCommentRequest request) {
        BitstreamComment existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        existing.setText(request.getComment());
        existing.setBitstreamId(request.getBitstreamId());

        BitstreamComment saved = repository.save(existing);
        return mapToResponse(context,saved);
    }

    @Override
    public void delete(int id) {
        BitstreamComment comment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        comment.setDeleted(true);
        repository.save(comment);
    }

    @Override
    public BitstreamCommentResponse getById(Context context , int id) {
        BitstreamComment comment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        return mapToResponse(context,comment);
    }

//    @Override
//    public List<BitstreamCommentResponse> getByBitstreamId(UUID bitstreamId) {
//        return repository.findByBitstreamId(bitstreamId)
//                .stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }
//
//    private BitstreamCommentResponse mapToResponse(BitstreamComment comment) {
//        EPerson user = ePersonService.find(context, comment.getCommenterId());
//        String commenterName = (user != null) ? user.getFullName() : "Unknown User";
//
//        return new BitstreamCommentResponse(
//                comment.getId(),
//                comment.getCommentDate(),
//                comment.getText(),
//                comment.isDeleted(),
//
//        );
//    }

    @Override
    public List<BitstreamCommentResponse> getByBitstreamId(Context context, UUID bitstreamId) {
        return repository.findByBitstreamIdAndIsDeletedFalse(bitstreamId)
                .stream()
                .map(comment -> mapToResponse(context, comment))
                .collect(Collectors.toList());
    }

    private BitstreamCommentResponse mapToResponse(Context context, BitstreamComment comment) {
        EPerson user = null;
        try {
            user = ePersonService.find(context, comment.getCommenterId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String commenterName = (user != null) ? user.getFullName() : "Unknown User";

        return new BitstreamCommentResponse(
                comment.getId(),
                comment.getCommentDate(),
                comment.getText(),
                comment.isDeleted(),
                commenterName
        );
    }

}

