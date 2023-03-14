package com.ks.sd.api.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ks.sd.api.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserId(String userId);
    Optional<List<User>> findByDelYn(String delYn);
    Optional<List<User>> findByTeamTeamNo(Integer teamNo);
}
