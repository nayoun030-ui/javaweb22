package com.example.demo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository; // JPA 기본 메서드 제공
import org.springframework.stereotype.Repository; // 리포지토리 어노테이션
import com.example.demo.model.domain.TestDB; // 도메인(엔티티) 연결

@Repository // 리포지토리 등록
public interface TestRepository extends JpaRepository<TestDB, Long> {

    // 메서드 이름 규칙 → SELECT * FROM testdb WHERE name = ?
    TestDB findByName(String name);
}
