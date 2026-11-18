package com.example.demo.model.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity // TestDB라는 객체와 DB 테이블을 매핑. JPA가 관리
@Table(name = "testdb") // 테이블 이름은 testdb
@Data // set/get/tostring 등 필수 메서드 자동 생성
public class TestDB {

    @Id // 해당 변수가 PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동 증가
    private Long id;

    @Column(nullable = true) // 이름 : null 허용
    private String name;

    // [4주차 연습문제] 나이, 성별 컬럼을 여기에 추가해 보세요.
}
