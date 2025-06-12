// src/main/java/com/snuggle/homework/controller/ErrorResponse.java
package com.snuggle.homework.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
}
