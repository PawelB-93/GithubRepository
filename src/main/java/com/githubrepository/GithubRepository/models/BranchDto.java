package com.githubrepository.GithubRepository.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BranchDto {
    @JsonProperty(value = "name")
    private final String name;
    @JsonProperty(value = "sha")
    private final String sha;

    public BranchDto(final Builder builder) {
        this.name = builder.name;
        this.sha = builder.sha;
    }

    public String getName() {
        return this.name;
    }

    public String getSha() {
        return this.sha;
    }

    public static class Builder {
        private String name;
        private String sha;

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withSha(final String sha) {
            this.sha = sha;
            return this;
        }

        public BranchDto build() {
            return new BranchDto(this);
        }
    }
}
