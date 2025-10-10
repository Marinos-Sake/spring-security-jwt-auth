package io.github.marinossake.mapper;

import io.github.marinossake.dto.AttachmentReadOnlyDTO;
import io.github.marinossake.entity.Attachment;
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