package com.example.demo.model.dto;

import com.example.demo.model.domain.Member;

// [7주차] API 응답용 DTO : 화면(JSON)에 내보낼 값만 담는다 → password 는 빠진다
// record (자바 16+) : 필드 · 생성자 · getter · equals · toString 을 한 줄로
public record MemberResponse(Long id, String username, String name, String role) {

    public static MemberResponse from(Member member) { // 엔티티 → 응답 DTO 변환
        return new MemberResponse(member.getId(), member.getUsername(),
                                  member.getName(), member.getRole());
    }
}
