package com.githubrepository.GithubRepository.models;

import java.util.List;

public class RepositoryDto {
    private final String repositoryName;
    private final String ownerLogin;
    private final List<BranchDto> branches;

    public RepositoryDto(final Builder builder) {
        this.repositoryName = builder.repositoryName;
        this.ownerLogin = builder.ownerLogin;
        this.branches = builder.branches;
    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public String getOwnerLogin() {
        return this.ownerLogin;
    }

    public List<BranchDto> getBranches() {
        return this.branches;
    }

    public static class Builder {
        private String repositoryName;
        private String ownerLogin;
        private List<BranchDto> branches;

        public Builder withRepositoryName(final String repositoryName) {
            this.repositoryName = repositoryName;
            return this;
        }

        public Builder withOwnerLogin(final String ownerLogin) {
            this.ownerLogin = ownerLogin;
            return this;
        }

        public Builder withBranches(final List<BranchDto> branches) {
            this.branches = branches;
            return this;
        }

        public RepositoryDto build() {
            return new RepositoryDto(this);
        }
    }
}
