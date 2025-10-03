package com.quizai.quizplatform.mapper;

import com.quizai.quizplatform.dto.AttachmentReadOnlyDTO;
import com.quizai.quizplatform.entity.Attachment;
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