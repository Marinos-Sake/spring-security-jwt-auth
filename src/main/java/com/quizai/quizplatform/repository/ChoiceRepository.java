package com.quizai.quizplatform.repository;

import com.quizai.quizplatform.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {}
