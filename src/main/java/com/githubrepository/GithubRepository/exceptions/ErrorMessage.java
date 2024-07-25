package com.githubrepository.GithubRepository.exceptions;

public class ErrorMessage {

    private String status;
    private String message;

    public ErrorMessage(final String status, final String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
