package com.quizai.quizplatform.core.exception;

import com.quizai.quizplatform.dto.ResponseMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(AppObjectAlreadyExistsException.class)
    public ResponseEntity<ResponseMessageDTO> handleAlreadyExists(AppObjectAlreadyExistsException ex) {
        ResponseMessageDTO response = new ResponseMessageDTO(
                LocalDateTime.now(),
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AppObjectNotFoundException.class)
    public ResponseEntity<ResponseMessageDTO> handleNotFound(AppObjectNotFoundException ex) {
        ResponseMessageDTO response = new ResponseMessageDTO(
                LocalDateTime.now(),
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AppObjectInvalidInputException.class)
    public ResponseEntity<ResponseMessageDTO> handleInvalidInput(AppObjectInvalidInputException ex) {
        ResponseMessageDTO response = new ResponseMessageDTO(
                LocalDateTime.now(),
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AppObjectValidationException.class)
    public ResponseEntity<ResponseMessageDTO> handleValidation(AppObjectValidationException ex) {
        ResponseMessageDTO response = new ResponseMessageDTO(
                LocalDateTime.now(),
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    ResponseEntity<Map<String,String>> handle(MaxUploadSizeExceededException e){
//        return ResponseEntity.status(413)
//                .body(Map.of("code","FILE_TOO_LARGE","message","Max 20MB"));
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<Map<String, String>> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                ))
                .toList();

        Map<String, Object> body = Map.of(
                "code", "VALIDATION_ERROR",
                "message", "One or more fields are invalid",
                "errors", validationErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

}
