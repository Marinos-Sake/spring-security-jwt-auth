package io.github.marinossake.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // μόνο parse/validate

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Read Authorization header and short-circuit if not Bearer
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // Parse token once; skip if invalid/expired or context already set
        var maybeClaims = jwtUtil.tryParse(token);
        if (maybeClaims.isEmpty() || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }


        var claims = maybeClaims.get();

        // Subject = public identifier of the user
        String publicId = claims.getSubject();

        // Expose publicId to downstream handlers if needed (non-sensitive)
        request.setAttribute("publicId", publicId);

        Object rolesClaim = claims.get("roles");
        List<String> roles = (rolesClaim instanceof List<?> rolesList)
                ? rolesList.stream().map(String.class::cast).toList()
                : List.of();

        String username = claims.get("username", String.class);

        // Build minimal principal from claims (no entity, no password)
        var principal = CustomUserPrincipal.fromClaims(publicId, username, roles);

        // Create an authenticated token with authorities from principal
        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
