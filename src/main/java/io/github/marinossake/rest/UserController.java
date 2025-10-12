package io.github.marinossake.rest;

import io.github.marinossake.dto.UserInsertDTO;
import io.github.marinossake.dto.UserReadOnlyDTO;
import io.github.marinossake.dto.UserUpdateDTO;
import io.github.marinossake.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/me")
    public ResponseEntity<UserReadOnlyDTO> getMyProfile(
            @AuthenticationPrincipal(expression = "publicId") String publicId
    ) {
        return ResponseEntity.ok(userService.getMyProfileByPublicId(publicId));
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(
            @AuthenticationPrincipal(expression = "publicId") String publicId,
            @Valid @RequestBody UserUpdateDTO dto
    ) {
        var out = userService.updateUser(publicId, dto);
        return out.changed()
                ? ResponseEntity.ok(out.body())
                : ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(
            @AuthenticationPrincipal(expression = "publicId") String publicId) {
        userService.deleteMyAccount(publicId);
        return ResponseEntity.noContent().build();
    }

}
