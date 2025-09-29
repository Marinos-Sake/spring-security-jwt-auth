package com.quizai.quizplatform.dto;

import com.quizai.quizplatform.core.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserReadOnlyDTO {

    private String publicId;

    private String username;

    private Boolean isActive;

    private UserRole role;

}
