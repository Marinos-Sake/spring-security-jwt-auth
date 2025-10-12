package io.github.marinossake.security;

import io.github.marinossake.core.enums.UserRole;
import io.github.marinossake.entity.User;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Minimal authenticated user snapshot used by Spring Security.
 * Carries identity and authorities; no heavy entity or PII.
 */
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
     * Factory for username/password authentication path (DaoAuthenticationProvider).
     * Includes password hash so Spring can verify credentials.
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
     * Factory for JWT path. No password, id may be null.
     * Expects logical roles like ["USER","ADMIN"]; prefixes to "ROLE_*".
     */
    public static CustomUserPrincipal fromClaims(String publicId, String username, List<String> roles) {
        var safeRoles = (roles == null) ? new ArrayList<String>() : roles;
        var authorities = safeRoles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new CustomUserPrincipal(
                null,   // no DB id available from token
                publicId,
                username,
                null,   // no credentials in JWT flow
                authorities,
                true    // treat as enabled; revoke via token strategy if needed
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

    /**
     * Clear sensitive credential after authentication completes.
     */
    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
