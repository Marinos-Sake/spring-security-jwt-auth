package io.github.marinossake.service;

import io.github.marinossake.dto.LoginRequestDTO;
import io.github.marinossake.dto.LoginResponseDTO;
import io.github.marinossake.security.CustomUserPrincipal;
import io.github.marinossake.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    public LoginResponseDTO login(LoginRequestDTO requestDTO) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                )
        );

        var principal = (CustomUserPrincipal) authentication.getPrincipal();

        var roleNames = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())                 // "ROLE_ADMIN"
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r) // -> "ADMIN"
                .toList();

        String token = jwtUtil.generateToken(
                principal.getPublicId(),
                principal.getUsername(),
                roleNames
        );

        return new LoginResponseDTO(token);

    }


}
