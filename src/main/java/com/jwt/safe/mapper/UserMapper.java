package com.jwt.safe.mapper;

import com.jwt.safe.dto.UserInsertDTO;
import com.jwt.safe.dto.UserReadOnlyDTO;
import com.jwt.safe.dto.UserUpdateDTO;
import com.jwt.safe.entity.User;
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

    public void updateUserFromDTO(UserUpdateDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
    }
}
