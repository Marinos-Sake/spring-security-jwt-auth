package com.quizai.quizplatform.mapper;

import com.quizai.quizplatform.dto.UserInsertDTO;
import com.quizai.quizplatform.dto.UserReadOnlyDTO;
import com.quizai.quizplatform.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUserEntity(UserInsertDTO dto) {

        if (dto == null) return null;

        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());

        return user;
    }

    public UserReadOnlyDTO toUserReadOnly(User user) {
        if (user == null) return null;

        UserReadOnlyDTO dto = new UserReadOnlyDTO();

        dto.setPublicId(user.getPublicId());
        dto.setUsername(user.getUsername());
        dto.setIsActive(user.getIsActive());
        dto.setRole(user.getRole());

        return dto;
    }
}
