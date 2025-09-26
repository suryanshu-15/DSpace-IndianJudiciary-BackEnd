package org.dspace.app.rest.diracai.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.dto.Request.BitstreamCommentRequest;
import org.dspace.app.rest.diracai.dto.Response.BitstreamCommentResponse;
import org.dspace.app.rest.diracai.service.BitstreamCommentService;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bitstream/comment")
public class BitstreamCommentController {

    @Autowired
    private BitstreamCommentService service;

    @PostMapping
    public BitstreamCommentResponse create(@RequestBody BitstreamCommentRequest request, HttpServletRequest httpRequest) {
        Context context = ContextUtil.obtainContext(httpRequest);
        return service.create(context, request);
    }

    @PutMapping("/{id}")
    public BitstreamCommentResponse update(@PathVariable int id, @RequestBody BitstreamCommentRequest request, HttpServletRequest httpRequest) {
        Context context = ContextUtil.obtainContext(httpRequest);
        return service.update(context, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public BitstreamCommentResponse getById(@PathVariable int id,HttpServletRequest httpRequest) {
        Context context = ContextUtil.obtainContext(httpRequest);
        return service.getById(context,id);
    }

    @GetMapping("/bitstream/{bitstreamId}")
    public List<BitstreamCommentResponse> getByBitstream(@PathVariable UUID bitstreamId,HttpServletRequest httpRequest) {
        Context context = ContextUtil.obtainContext(httpRequest);
        return service.getByBitstreamId(context,bitstreamId);
    }
}
