package com.ks.sd.api.dp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.dp.entity.Deploy;

public interface DeployRepository extends JpaRepository<Deploy, Integer> {
    Optional<List<Deploy>> findByDelYnOrderByDpDtDescProjectAsc(String delYn);
}
