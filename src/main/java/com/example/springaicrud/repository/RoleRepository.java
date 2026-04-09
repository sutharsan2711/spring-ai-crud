package com.example.springaicrud.repository;

import com.example.springaicrud.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository
        extends JpaRepository<Role, Long> {

    // find role by name
    Optional<Role> findByName(String name);
}