package com.githubrepository.GithubRepository.models;

public class Commit {

    private String sha;

    public Commit(final String sha) {
        this.sha = sha;
    }

    public Commit() {
    }

    public String getSha() {
        return this.sha;
    }

    public void setSha(final String sha) {
        this.sha = sha;
    }
}
