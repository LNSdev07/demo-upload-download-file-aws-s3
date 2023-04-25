package com.example.demouploadanddowloadfile.repository;


import com.example.demouploadanddowloadfile.entity.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface FileDBRepository extends JpaRepository<FileDB, String> {
    void deleteByPath(String path);
}
