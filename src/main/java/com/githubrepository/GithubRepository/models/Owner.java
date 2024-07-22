package com.githubrepository.GithubRepository.models;

public class Owner {

    private String login;

    public Owner(final String login) {
        this.login = login;
    }

    public Owner() {
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }
}
