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
        builder.withRepositoryName(repository.name());
        builder.withOwnerLogin(repository.owner().login());
        final List<BranchDto> branches = repository.branches().stream().map(this::toBranchDto).toList();
        builder.withBranches(branches);
        return builder.build();
    }

    public BranchDto toBranchDto(final Branch branch) {
        final BranchDto.Builder builder = new BranchDto.Builder();
        builder.withName(branch.name());
        builder.withSha(branch.commit().sha());
        return builder.build();
    }
}
