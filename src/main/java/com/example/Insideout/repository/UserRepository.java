package com.example.Insideout.repository;

import com.example.Insideout.entity.User;
import com.example.Insideout.entity.User.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    List<User> findAllByDeptCode(String deptCode);

    Page<User> findAllByDeptCode(String deptCode, Pageable pageable);

    Page<User> findAllByDeptCodeAndRole(String deptCode, Role role, Pageable pageable);

    void deleteByUserId(String userId);

    boolean existsByUserId(String userId);
}
