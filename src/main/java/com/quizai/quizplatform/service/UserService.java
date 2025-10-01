package com.quizai.quizplatform.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.quizai.quizplatform.core.exception.AppObjectAlreadyExistsException;
import com.quizai.quizplatform.core.exception.AppObjectNotFoundException;
import com.quizai.quizplatform.dto.UserInsertDTO;
import com.quizai.quizplatform.dto.UserReadOnlyDTO;
import com.quizai.quizplatform.dto.UserUpdateDTO;
import com.quizai.quizplatform.entity.User;
import com.quizai.quizplatform.mapper.UserMapper;
import com.quizai.quizplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final Cache<String, UserDetails> userDetailsCache;
    private final Cache<String, UserReadOnlyDTO> userProfileCache;

    @Transactional
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

    @Transactional(readOnly = true)
    public UserReadOnlyDTO getMyProfileByPublicId(String publicId) {
        UserReadOnlyDTO cached = userProfileCache.getIfPresent(publicId);
        if (cached != null) {
            log.info("Profile cache hit for publicId: {}", publicId);
            return cached;
        }

        log.info("Profile cache miss for publicId: {}", publicId);

        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER_", "User not found"));

        UserReadOnlyDTO dto = userMapper.toUserReadOnly(user);
        userProfileCache.put(publicId, dto);
        return dto;
    }


    public record UpdateOutcome<T>(boolean changed, T body) {
        public static <T> UpdateOutcome<T> changed(T body){ return new UpdateOutcome<>(true, body); }
        public static <T> UpdateOutcome<T> noChange(T body){ return new UpdateOutcome<>(false, body); }
    }

    @Transactional
    public UpdateOutcome<UserReadOnlyDTO> updateUser(String publicId, UserUpdateDTO dto) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER_", "User not found"));

        boolean dirty = false;

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new AppObjectAlreadyExistsException("USER_", "Username already exists");
            }
            user.setUsername(dto.getUsername());
            dirty = true;
        }
        if (dto.getRole() != null && dto.getRole() != user.getRole()) {
            user.setRole(dto.getRole());
            dirty = true;
        }
        if (dto.getPassword() != null) {
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
                dirty = true;
            }
        }

        if (!dirty) {
            return UpdateOutcome.noChange(userMapper.toUserReadOnly(user));
        }

        User saved = userRepository.save(user);
        userDetailsCache.invalidate(saved.getPublicId());
        UserReadOnlyDTO updatedDto = userMapper.toUserReadOnly(saved);
        userProfileCache.put(saved.getPublicId(), updatedDto);
        log.info("Invalidated userDetailsCache and updated userProfileCache for publicId: {}", saved.getPublicId());
        log.info("User updated successfully for publicId: {}", saved.getPublicId());

        return UpdateOutcome.changed(updatedDto);
    }

    @Transactional
    public void deleteMyAccount(String publicId) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER_", "User not found"));
        userRepository.delete(user);
        userDetailsCache.invalidate(publicId);
        userProfileCache.invalidate(publicId);
        log.info("User deleted and caches invalidated for publicId: {}", publicId);
    }
}
