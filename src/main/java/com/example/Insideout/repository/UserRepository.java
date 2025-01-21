package com.example.Insideout.repository;

import com.example.Insideout.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    List<User> findAllByDeptCode(String deptCode);
}
