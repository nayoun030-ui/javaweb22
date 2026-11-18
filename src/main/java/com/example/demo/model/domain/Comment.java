package com.example.demo.model.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity // [10주차] 댓글 엔티티 : comments 테이블
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호 자동 증가
    private Long id;

    @Column(nullable = false, length = 500) // 댓글 내용 : 필수, 500자까지 → varchar(500) not null
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // 어느 글의 댓글인지 (글 1 : 댓글 N) — 외래 키를 가진 쪽 = 주인
    @JoinColumn(name = "post_id", nullable = false) // 외래 키 → posts.id
    @ToString.Exclude @EqualsAndHashCode.Exclude // 글 ↔ 댓글 양방향 : toString 무한 반복 방지
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) // 댓글 작성자 (9주차 글 작성자와 같은 방법)
    @JoinColumn(name = "member_id", nullable = false) // 외래 키 → member.id
    private Member author;

    @Column(nullable = false) // 작성 시간
    private LocalDateTime createdAt;
}
