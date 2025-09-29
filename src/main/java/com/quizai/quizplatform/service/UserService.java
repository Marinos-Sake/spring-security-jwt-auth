package com.quizai.quizplatform.service;

import com.quizai.quizplatform.core.exception.AppObjectAlreadyExistsException;
import com.quizai.quizplatform.dto.UserInsertDTO;
import com.quizai.quizplatform.dto.UserReadOnlyDTO;
import com.quizai.quizplatform.entity.User;
import com.quizai.quizplatform.mapper.UserMapper;
import com.quizai.quizplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserReadOnlyDTO createUser(UserInsertDTO dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new AppObjectAlreadyExistsException(
                    "USER_",
                    "Username already exists"
            );
        }

        User user = userMapper.toUserEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserReadOnly(savedUser);
    }
}
