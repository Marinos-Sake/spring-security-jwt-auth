package io.github.marinossake.mapper;

import io.github.marinossake.dto.UserInsertDTO;
import io.github.marinossake.dto.UserReadOnlyDTO;
import io.github.marinossake.dto.UserUpdateDTO;
import io.github.marinossake.entity.User;
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

    public void applyUserUpdate(UserUpdateDTO dto, User user, String encodedPasswordOrNull) {
        if (dto == null || user == null) return;
        if (dto.getUsername() != null) user.setUsername(dto.getUsername()); // ήδη trimmed/validated από service
        if (encodedPasswordOrNull != null) user.setPassword(encodedPasswordOrNull);
    }
}
