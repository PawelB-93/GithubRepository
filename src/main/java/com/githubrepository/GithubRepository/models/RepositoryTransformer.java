package com.githubrepository.GithubRepository.models;

import java.util.List;

public class RepositoryTransformer {

    public RepositoryDto toRepositoryDto(final Repository repository) {
        RepositoryDto.Builder builder = new RepositoryDto.Builder();
        builder.withRepositoryName(repository.getName());
        builder.withOwnerLogin(repository.getOwner().getLogin());
        final List<BranchDto> branches = repository.getBranches().stream().map(this::toBranchDto).toList();
        builder.withBranches(branches);
        return builder.build();
    }

    public BranchDto toBranchDto(final Branch branch) {
        BranchDto.Builder builder = new BranchDto.Builder();
        builder.withName(branch.getName());
        builder.withSha(branch.getCommit().getSha());
        return builder.build();
    }
}
