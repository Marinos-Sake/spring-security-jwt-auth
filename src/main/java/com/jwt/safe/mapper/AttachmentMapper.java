package com.jwt.safe.mapper;

import com.jwt.safe.dto.AttachmentReadOnlyDTO;
import com.jwt.safe.entity.Attachment;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {


    public AttachmentReadOnlyDTO mapToDTO(Attachment attachment) {
        if (attachment == null) return null;

        AttachmentReadOnlyDTO dto = new AttachmentReadOnlyDTO();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setContentType(attachment.getContentType());
        dto.setExtension(attachment.getExtension());
        return dto;
    }
}