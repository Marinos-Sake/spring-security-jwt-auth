package com.jwt.safe.rest;



import com.jwt.safe.dto.AttachmentReadOnlyDTO;
import com.jwt.safe.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/attachment")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AttachmentReadOnlyDTO> upload(
            @AuthenticationPrincipal(expression = "publicId") String publicId,
            @RequestPart("file") MultipartFile file
    ) {
        AttachmentReadOnlyDTO dto = attachmentService.upload(publicId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AttachmentReadOnlyDTO>> getMyAttachments(
            @AuthenticationPrincipal(expression = "publicId") String publicId
    ) {
        return ResponseEntity.ok(attachmentService.getMyAttachments(publicId));
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal(expression = "publicId") String publicId,
            @PathVariable("attachmentId") Long attachmentId
    ) {
        attachmentService.delete(publicId, attachmentId);
        return ResponseEntity.noContent().build();
    }


}
