package com.quizai.quizplatform.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "choices")
public class Choice extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "text", nullable = false, length = 1000)
    private String text;

    @Column(name = "correct", nullable = false)
    private Boolean correct = Boolean.FALSE;

    @Column(name = "choice_order", nullable = false)
    private Integer choiceOrder;
}
