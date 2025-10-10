package io.github.marinossake.service;

import io.github.marinossake.core.exception.AppObjectNotFoundException;
import io.github.marinossake.core.exception.AppObjectValidationException;
import io.github.marinossake.dto.AttachmentReadOnlyDTO;
import io.github.marinossake.entity.Attachment;
import io.github.marinossake.entity.User;
import io.github.marinossake.mapper.AttachmentMapper;
import io.github.marinossake.repository.AttachmentRepository;
import io.github.marinossake.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final AttachmentMapper attachmentMapper;

    private static final Path ROOT_DIRECTORY = Paths.get("uploads");

    @Transactional
    public AttachmentReadOnlyDTO upload(String publicId, MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new AppObjectValidationException("ATTACHMENT_", "Empty file");
        }

        ensureRootDirectory();


        String raw = multipartFile.getOriginalFilename();
        String cleaned = StringUtils.cleanPath(raw != null ? raw : "file.pdf");
        Path candidate = Paths.get(cleaned);
        if (candidate.isAbsolute() || cleaned.contains("..")) {
            throw new AppObjectValidationException("ATTACHMENT_", "Invalid filename");
        }
        String originalFileName = candidate.getFileName().toString();


        String extension = extractExtension(originalFileName);
        if (!"pdf".equalsIgnoreCase(extension)) {
            throw new AppObjectValidationException("ATTACHMENT_", "Only PDF is allowed");
        }


        String contentType = multipartFile.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new AppObjectValidationException("ATTACHMENT_", "Only PDF is allowed");
        }


        String savedName = UUID.randomUUID().toString() + ".pdf";
        Path targetPath = ROOT_DIRECTORY.resolve(savedName).normalize();
        if (!targetPath.toAbsolutePath().startsWith(ROOT_DIRECTORY.toAbsolutePath())) {
            throw new AppObjectValidationException("ATTACHMENT_", "Invalid target path");
        }

        try {
            Files.copy(multipartFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AppObjectValidationException("ATTACHMENT_", "Failed to save file");
        }

        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER_", "User not found"));

        Attachment attachment = new Attachment();
        attachment.setFileName(originalFileName);
        attachment.setSavedName(savedName);
        attachment.setExtension("pdf");
        attachment.setContentType("application/pdf");
        attachment.setFilePath(targetPath.toAbsolutePath().toString());
        attachment.setUser(user);

        Attachment saved = attachmentRepository.save(attachment);
        log.info("Attachment saved: id={}, userPublicId={}, path={}", saved.getId(), publicId, saved.getFilePath());

        return attachmentMapper.mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AttachmentReadOnlyDTO> getMyAttachments(String publicId) {
        List<Attachment> attachments = attachmentRepository.findAllByUserPublicIdOrderByIdDesc(publicId);
        return attachments.stream().map(attachmentMapper::mapToDTO).toList();
    }

    @Transactional
    public void delete(String publicId, Long attachmentId) {
        Attachment attachment = attachmentRepository.findByIdAndUserPublicId(attachmentId, publicId)
                .orElseThrow(() -> new AppObjectNotFoundException("ATTACHMENT_", "Attachment not found"));

        try {
            if (attachment.getFilePath() != null) {
                Files.deleteIfExists(Paths.get(attachment.getFilePath()));
            }
        } catch (Exception ex) {
            log.warn("Failed to delete file on disk for attachmentId={}: {}", attachmentId, ex.getMessage());
        }

        attachmentRepository.delete(attachment);
        log.info("Attachment deleted: id={}, userPublicId={}", attachmentId, publicId);
    }

    private void ensureRootDirectory() {
        try {
            if (!Files.exists(ROOT_DIRECTORY)) {
                Files.createDirectories(ROOT_DIRECTORY);
            }
        } catch (IOException e) {
            throw new AppObjectValidationException("ATTACHMENT_", "Failed to initialize upload directory");
        }
    }

    private String extractExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot <= 0 || dot == fileName.length() - 1) return "";
        return fileName.substring(dot + 1).toLowerCase();
    }
}
