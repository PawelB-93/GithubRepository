package com.githubrepository.GithubRepository.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class OwnerNotFoundException extends ResponseStatusException {

    public OwnerNotFoundException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
