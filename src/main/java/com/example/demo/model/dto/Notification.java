package com.example.demo.model.dto;

// [12주차] 단계 4 : 개인 알림 (서버 → 한 사람) : to = 받을 사람 로그인 아이디, postId = 누르면 갈 모집글
public record Notification(String to, String text, Long postId) {
}
