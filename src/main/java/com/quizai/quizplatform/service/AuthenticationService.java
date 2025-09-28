package com.quizai.quizplatform.service;

import com.quizai.quizplatform.dto.LoginRequestDTO;
import com.quizai.quizplatform.dto.LoginResponseDTO;
import com.quizai.quizplatform.security.CustomUserPrincipal;
import com.quizai.quizplatform.security.JwtUtil;
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
