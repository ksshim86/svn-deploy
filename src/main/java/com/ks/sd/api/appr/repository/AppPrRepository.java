package com.ks.sd.api.appr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.appr.entity.AppPr;
import com.ks.sd.api.appr.entity.AppPrId;
import com.ks.sd.api.pjt.entity.Project;

public interface AppPrRepository extends JpaRepository<AppPr, AppPrId> {
    Optional<List<AppPr>> findByProject(Project project);
    void deleteByProject(Project project);
}
