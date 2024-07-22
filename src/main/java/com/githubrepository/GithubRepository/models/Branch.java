package com.githubrepository.GithubRepository.models;

public class Branch {

    private String name;
    private Commit commit;

    public Branch(final String name, final Commit commit) {
        this.name = name;
        this.commit = commit;
    }

    public Branch() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Commit getCommit() {
        return this.commit;
    }

    public void setCommit(final Commit commit) {
        this.commit = commit;
    }
}
