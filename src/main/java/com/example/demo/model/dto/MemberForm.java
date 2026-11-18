package com.example.demo.model.dto;

import lombok.Data;

@Data // set/get/tostring 등 필수 메서드 자동 생성
public class MemberForm { // 회원가입 화면 → 컨트롤러로 전달되는 데이터

    private String username;        // input name="username"
    private String password;        // input name="password"
    private String passwordConfirm; // input name="passwordConfirm" (확인용, 저장 안 함)
    private String name;            // input name="name"
    // role 필드는 없다 → 화면에서 role=ADMIN 을 몰래 보내도 무시된다 (서버가 USER 로 지정)
}
