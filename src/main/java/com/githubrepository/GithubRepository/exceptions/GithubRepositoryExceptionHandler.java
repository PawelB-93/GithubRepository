package com.githubrepository.GithubRepository.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GithubRepositoryExceptionHandler {

    @ExceptionHandler({OwnerNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleOwnerNotFoundException(final OwnerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(String.valueOf(e.getStatusCode().value()), e.getReason()));
    }
}
