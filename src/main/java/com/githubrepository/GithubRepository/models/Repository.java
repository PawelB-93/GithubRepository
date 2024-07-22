package com.githubrepository.GithubRepository.models;

import java.util.List;

public class Repository {

    private String name;
    private Owner owner;
    private List<Branch> branches;

    public Repository(final String name, final Owner owner, final List<Branch> branches) {
        this.name = name;
        this.owner = owner;
        this.branches = branches;
    }

    public Repository() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return this.owner;
    }

    public void setOwner(final Owner owner) {
        this.owner = owner;
    }

    public List<Branch> getBranches() {
        return this.branches;
    }

    public void setBranches(final List<Branch> branches) {
        this.branches = branches;
    }
}
