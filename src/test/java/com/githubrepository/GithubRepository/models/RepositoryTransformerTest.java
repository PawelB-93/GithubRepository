package com.githubrepository.GithubRepository.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RepositoryTransformerTest {

    private RepositoryTransformer transformer;

    @BeforeEach
    public void init() {
        this.transformer = new RepositoryTransformer();
    }

    @Test
    public void testToRepositoryDtoShouldReturnRepositoryDto() {
        //GIVEN
        final Commit commit = new Commit("123456");
        final Branch branch = new Branch("master", commit);
        final List<Branch> branches = List.of(branch);
        final Owner owner = new Owner("Owner1");
        final Repository repository = new Repository("Project1", owner, branches);

        //WHEN
        final RepositoryDto dto = transformer.toRepositoryDto(repository);

        //THEN
        assertThat(dto).isNotNull();
        assertThat(dto.getRepositoryName()).isEqualTo("Project1");
        assertThat(dto.getOwnerLogin()).isEqualTo("Owner1");
        assertThat(dto.getBranches().get(0).getName()).isEqualTo("master");
        assertThat(dto.getBranches().get(0).getSha()).isEqualTo("123456");
    }

    @Test
    public void testToBranchDtoShouldReturnBranchDto() {
        // Given
        final Commit commit = new Commit("123456");
        final Branch branch = new Branch("master", commit);

        // When
        final BranchDto dto = transformer.toBranchDto(branch);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("master");
        assertThat(dto.getSha()).isEqualTo("123456");
    }

}