package com.jwt.safe.service;

import com.jwt.safe.dto.LoginRequestDTO;
import com.jwt.safe.dto.LoginResponseDTO;
import com.jwt.safe.security.CustomUserPrincipal;
import com.jwt.safe.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

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

        String token = jwtUtil.generateToken(principal.getPublicId());

        return new LoginResponseDTO(token);

    }


}
