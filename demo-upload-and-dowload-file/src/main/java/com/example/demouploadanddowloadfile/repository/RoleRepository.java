package com.example.demouploadanddowloadfile.repository;

import com.example.demouploadanddowloadfile.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
