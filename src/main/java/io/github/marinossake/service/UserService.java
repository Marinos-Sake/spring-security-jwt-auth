package io.github.marinossake.service;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.marinossake.core.exception.AppObjectAlreadyExistsException;
import io.github.marinossake.core.exception.AppObjectNotFoundException;
import io.github.marinossake.dto.UserInsertDTO;
import io.github.marinossake.dto.UserReadOnlyDTO;
import io.github.marinossake.dto.UserUpdateDTO;
import io.github.marinossake.entity.User;
import io.github.marinossake.mapper.UserMapper;
import io.github.marinossake.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        StringBuilder changes = new StringBuilder();


        if (dto.getUsername() != null) {
            String newU = dto.getUsername().trim();
            if (!newU.equals(user.getUsername())) {
                if (userRepository.existsByUsername(newU))
                    throw new AppObjectAlreadyExistsException("USER_", "Username already exists");
                dto.setUsername(newU);
                dirty = true;
                changes.append("username to ").append(newU).append(", ");
            } else {
                dto.setUsername(null);
            }
        }

        // password rules
        String encodedPwd = null;
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                encodedPwd = passwordEncoder.encode(dto.getPassword());
                dirty = true;
                changes.append("password, ");
            }
        }

        if (!dirty) return UpdateOutcome.noChange(userMapper.toUserReadOnly(user));


        userMapper.applyUserUpdate(dto, user, encodedPwd);

        User saved = userRepository.save(user);
        UserReadOnlyDTO out = userMapper.toUserReadOnly(saved);
        userProfileCache.put(saved.getPublicId(), out);
        log.info("User updated for publicId: {} with changes: {}", saved.getPublicId(), changes);
        return UpdateOutcome.changed(out);
    }


    @Transactional
    public void deleteMyAccount(String publicId) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER_", "User not found"));
        userRepository.delete(user);
        userProfileCache.invalidate(publicId);
        log.info("User deleted and caches invalidated for publicId: {}", publicId);
    }
}
