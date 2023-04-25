package com.example.demouploadanddowloadfile.repository;


import com.example.demouploadanddowloadfile.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    boolean existsById(String id);
}
