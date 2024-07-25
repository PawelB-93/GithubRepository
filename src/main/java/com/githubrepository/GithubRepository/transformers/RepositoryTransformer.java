package com.githubrepository.GithubRepository.transformers;

import com.githubrepository.GithubRepository.models.Branch;
import com.githubrepository.GithubRepository.models.BranchDto;
import com.githubrepository.GithubRepository.models.Repository;
import com.githubrepository.GithubRepository.models.RepositoryDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RepositoryTransformer {

    public RepositoryDto toRepositoryDto(final Repository repository) {
        final RepositoryDto.Builder builder = new RepositoryDto.Builder();
        builder.withRepositoryName(repository.getName());
        builder.withOwnerLogin(repository.getOwner().getLogin());
        final List<BranchDto> branches = repository.getBranches().stream().map(this::toBranchDto).toList();
        builder.withBranches(branches);
        return builder.build();
    }

    public BranchDto toBranchDto(final Branch branch) {
        final BranchDto.Builder builder = new BranchDto.Builder();
        builder.withName(branch.getName());
        builder.withSha(branch.getCommit().getSha());
        return builder.build();
    }
}
