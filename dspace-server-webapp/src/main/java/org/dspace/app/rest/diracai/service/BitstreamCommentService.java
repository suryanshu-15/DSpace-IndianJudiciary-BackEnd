package org.dspace.app.rest.diracai.service;

import org.dspace.app.rest.diracai.dto.Request.BitstreamCommentRequest;
import org.dspace.app.rest.diracai.dto.Response.BitstreamCommentResponse;
import org.dspace.core.Context;

import java.util.List;
import java.util.UUID;

public interface BitstreamCommentService {
    BitstreamCommentResponse create(Context context, BitstreamCommentRequest request);
    BitstreamCommentResponse update(Context context, int id, BitstreamCommentRequest request);
    void delete(int id);
    BitstreamCommentResponse getById(Context context,int id);
    List<BitstreamCommentResponse> getByBitstreamId(Context context,UUID bitstreamId);
}
