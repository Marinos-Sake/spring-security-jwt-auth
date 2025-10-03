package com.quizai.quizplatform.repository;

import com.quizai.quizplatform.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {


    List<Attachment> findAllByUserPublicIdOrderByIdDesc(String publicId);

    Optional<Attachment> findByIdAndUserPublicId(Long id, String publicId);
}
