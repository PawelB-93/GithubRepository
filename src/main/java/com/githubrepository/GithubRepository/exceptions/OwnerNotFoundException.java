package com.githubrepository.GithubRepository.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class OwnerNotFoundException extends HttpClientErrorException {

    public OwnerNotFoundException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
