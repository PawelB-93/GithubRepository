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
    final List<BranchDto> branches = repository.branches().stream().map(this::toBranchDto).toList();
    return new RepositoryDto(repository.name(), repository.owner().login(), branches);
}

    public BranchDto toBranchDto(final Branch branch) {
        return new BranchDto(branch.name(), branch.commit().sha());
    }
}
