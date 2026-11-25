package com.example.demo.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.model.domain.Member;
import com.example.demo.model.domain.Post;

@Repository // 게시글 리포지토리 : save, findAll, findById, delete, findAll(pageable) 은 이미 있다
public interface PostRepository extends JpaRepository<Post, Long> {

    // [10주차] 단계 2 : 제목 또는 내용에 검색어가 들어간 글 (JPQL : 테이블 posts 가 아니라 엔티티 Post)
    // :keyword 는 값이 들어갈 "자리" → 파라미터 바인딩 (SQL 인젝션 방지)
    // Pageable 을 맨 뒤에 두면 ORDER BY · LIMIT · count 를 스프링이 붙여 준다
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> search(@Param("keyword") String keyword, Pageable pageable);

    // [10주차] 단계 6 : 이 회원이 쓴 글이 있는지 (1개만 찾으면 멈춘다)
    boolean existsByAuthor(Member author);

    // 메인 화면 숫자 : 지금 모집 중인 글 (정원이 있고 아직 덜 찬 글) — 두 칼럼 비교는 메서드 이름으로 못 만들어서 JPQL
    @Query("SELECT COUNT(p) FROM Post p WHERE p.capacity IS NOT NULL AND p.appliedCount < p.capacity")
    long countOpenRecruits();

    // [9주차 연습문제 ②] 내가 쓴 글 : findBy + Author_Username + OrderByIdDesc
    // [10주차 연습문제 ①] 내가 쓴 글 페이징 : 마지막에 Pageable 만 더하면 Page 가 된다
}
