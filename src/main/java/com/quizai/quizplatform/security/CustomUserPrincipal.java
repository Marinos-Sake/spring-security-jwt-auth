package com.quizai.quizplatform.security;

import com.quizai.quizplatform.core.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.quizai.quizplatform.entity.User;

import java.util.Collection;
import java.util.List;

public class CustomUserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String publicId;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    private CustomUserPrincipal(Long id,
                                String publicId,
                                String username,
                                String password,
                                Collection<? extends GrantedAuthority> authorities,
                                boolean enabled) {
        this.id = id;
        this.publicId = publicId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    /** Mapping: Entity User -> Principal */
    public static CustomUserPrincipal from(User user) {
        UserRole role = user.getRole();

        List<GrantedAuthority> authorization = List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );

        return new CustomUserPrincipal(
                user.getId(),
                user.getPublicId(),
                user.getUsername(),
                user.getPassword(),
                authorization,
                user.isActive()
        );
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


}
