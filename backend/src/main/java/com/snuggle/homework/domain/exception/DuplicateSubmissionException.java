// src/main/java/com/snuggle/homework/domain/exception/DuplicateSubmissionException.java
package com.snuggle.homework.domain.exception;

public class DuplicateSubmissionException extends RuntimeException {
    public DuplicateSubmissionException() {
        super();
    }

    public DuplicateSubmissionException(String message) {
        super(message);
    }
}
