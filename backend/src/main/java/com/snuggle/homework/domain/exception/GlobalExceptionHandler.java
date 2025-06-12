package com.snuggle.homework.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateSubmissionException.class)
    public ResponseEntity<ErrorResponse> handleDup(DuplicateSubmissionException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("ALREADY_SUBMITTED", "오늘은 이미 제출했습니다."));
    }
}
