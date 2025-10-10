package com.jwt.safe.security;

import com.jwt.safe.core.enums.UserRole;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.jwt.safe.entity.User;

import java.util.Collection;
import java.util.List;

public class CustomUserPrincipal implements UserDetails, CredentialsContainer {

    private final Long id;
    private final String username;
    private final String publicId;
    private String password;
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

    /**
     * Factory used only during username/password login.
     * We must include the encoded password here so that the DaoAuthenticationProvider
     * can verify credentials using PasswordEncoder.matches(...).
     * After successful authentication, Spring automatically calls eraseCredentials()
     * so the password is nulled and not kept in memory longer than necessary.
     */

    public static CustomUserPrincipal fromForLogin(User user) {
        return new CustomUserPrincipal(
                user.getId(),
                user.getPublicId(),
                user.getUsername(),
                user.getPassword(),
                buildAuthorities(user.getRole()),
                user.getIsActive()
        );
    }

    /**
     * Factory used when building a principal from a JWT (publicId lookup).
     * No password is required in this flow because authentication has already
     * been verified when the token was issued.
     * Keeping the password here would be unnecessary and would expose sensitive data
     * in memory for no reason, so we always set it to null.
     */
    public static CustomUserPrincipal fromForJwt(User user) {
        return new CustomUserPrincipal(
                user.getId(),
                user.getPublicId(),
                user.getUsername(),
                null,
                buildAuthorities(user.getRole()),
                user.getIsActive()
        );

    }
    private static List<GrantedAuthority> buildAuthorities(UserRole role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
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

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

}
