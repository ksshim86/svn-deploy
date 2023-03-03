package com.ks.sd.api.pjt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ks.sd.api.pjt.dto.ProjectSearchRequest;
import com.ks.sd.api.pjt.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("SELECT p FROM Project p WHERE (:#{#request.delYn} IS NULL OR p.delYn = :#{#request.delYn})")
    Optional<List<Project>> searchProjects(@Param("request") ProjectSearchRequest projectSearchRequest);

    void deleteByPjtNo(Integer pjtNo);
}
