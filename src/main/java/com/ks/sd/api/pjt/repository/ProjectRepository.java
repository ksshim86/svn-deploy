package com.ks.sd.api.pjt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ks.sd.api.pjt.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project> {
    Optional<List<Project>> findByDelYn(String delYn);

    Optional<List<Project>> findByDelYnAndDpStAndStartedYnAndRcsSt(String delYn, String dpSt, String startedYn, String rcsSt);
    
    void deleteByPjtNo(Integer pjtNo);
}
