package com.githubrepository.GithubRepository.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RepositoryDto(@JsonProperty(value = "name") String repositoryName,
                            @JsonProperty(value = "login") String ownerLogin,
                            @JsonProperty(value = "branches") List<BranchDto> branches) {
}
