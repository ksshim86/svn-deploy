package com.ks.sd.api.pjt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;
import com.ks.sd.api.pjt.entity.SubProjectId;

public interface SubProjectRepository extends JpaRepository<SubProject, SubProjectId> {
    Optional<List<SubProject>> findByProject(Project project);
    Integer countByProject(Project project);
    Optional<List<SubProject>> findByProjectAndDelYn(Project project, String delYn);
    Optional<SubProject> findByProjectAndSubPjtNo(Project project, Integer subPjtNo);
    void deleteByProject(Project project);
}
