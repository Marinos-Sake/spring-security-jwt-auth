package com.quizai.quizplatform.rest;

import com.quizai.quizplatform.dto.UserInsertDTO;
import com.quizai.quizplatform.dto.UserReadOnlyDTO;
import com.quizai.quizplatform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserReadOnlyDTO> createUser(@Valid @RequestBody UserInsertDTO dto) {
        UserReadOnlyDTO created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


}
