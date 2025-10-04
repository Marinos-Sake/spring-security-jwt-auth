package com.quizai.quizplatform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceReadOnlyDTO {

    private Long id;

    private String text;

    private Boolean correct;

    private Integer choiceOrder;

}
