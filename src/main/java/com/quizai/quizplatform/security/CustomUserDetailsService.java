package com.quizai.quizplatform.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.quizai.quizplatform.entity.User;
import com.quizai.quizplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Cache<String, UserDetails> userDetailsCache;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return CustomUserPrincipal.fromForLogin(user);
    }

    public UserDetails loadUserByPublicId(String publicId) throws UsernameNotFoundException {
        return userDetailsCache.get(publicId, id -> {
            User user = userRepository.findByPublicId(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
            return CustomUserPrincipal.fromForJwt(user);
        });
    }
}
