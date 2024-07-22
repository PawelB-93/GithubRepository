package com.githubrepository.GithubRepository.models;

public class BranchDto {
    private final String name;
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
