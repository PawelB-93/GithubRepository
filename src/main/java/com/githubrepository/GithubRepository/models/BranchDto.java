package com.githubrepository.GithubRepository.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BranchDto(@JsonProperty(value = "name") String name,
                        @JsonProperty(value = "sha") String sha) {
}
