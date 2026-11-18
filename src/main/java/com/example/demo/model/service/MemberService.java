package com.example.demo.model.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.model.domain.Member;
import com.example.demo.model.dto.MemberForm;
import com.example.demo.model.repository.MemberRepository;

@Service // 서비스 등록
public class MemberService implements UserDetailsService { // 로그인을 위한 인터페이스 제공

    @Autowired
    private MemberRepository memberRepository;

    @Autowired // SecurityConfig 에 등록한 BCryptPasswordEncoder 주입
    private PasswordEncoder passwordEncoder;

    public Member signup(MemberForm form) { // 회원가입
        // [7주차] 빈 값 검사 : Postman 의 JSON 요청에는 HTML 의 required 검사가 없다
        if (isBlank(form.getUsername()) || isBlank(form.getPassword()) || isBlank(form.getName())) {
            throw new IllegalArgumentException("아이디, 비밀번호, 이름은 필수입니다.");
        }
        // [5주차 연습문제 ②] 비밀번호 확인 : password 와 passwordConfirm 이 다르면
        //                   "비밀번호가 일치하지 않습니다." 예외를 던져 보세요. (DB 에 저장되면 안 됨)

        if (memberRepository.existsByUsername(form.getUsername())) { // ① 중복 확인
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        Member member = new Member();
        member.setUsername(form.getUsername());
        member.setPassword(passwordEncoder.encode(form.getPassword())); // ② ★ 암호화 1234 → $2a$10$...
        member.setName(form.getName());
        member.setRole("USER"); // ③ 기본 권한
        return memberRepository.save(member); // INSERT
    }

    @Override // 로그인 시 시큐리티가 자동 호출 (POST /login)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원 없음 : " + username));
        return User.builder() // 시큐리티용 사용자 객체로 변환
                .username(member.getUsername())
                .password(member.getPassword()) // 암호화 값 → matches() 로 시큐리티가 비교
                .roles(member.getRole()) // USER → ROLE_USER
                .build();
    }

    // ===================== [6주차] 권한 관리 =====================

    // 관리자가 부여할 수 있는 권한 (화면에서 넘어온 값을 그대로 믿지 않는다 : 화이트리스트)
    // [6주차 연습문제 ②] MANAGER 권한 추가
    private static final List<String> ROLES = List.of("USER", "ADMIN");

    public Member findByUsername(String username) { // 로그인한 아이디로 회원 조회 (/mypage)
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다 : " + username));
    }

    public List<Member> findAll() { // 전체 회원 (id 순서)
        return memberRepository.findAll(Sort.by("id"));
    }

    public Optional<Member> findById(Long id) { // [7주차] 회원 1명 (없으면 빈 Optional → API 404)
        return memberRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')") // 관리자만 실행 가능 (2차 잠금)
    public Member changeRole(Long id, String role) { // 권한 변경 ([7주차] void → Member : 바뀐 회원을 돌려준다)
        if (!ROLES.contains(role)) { // ① 허용된 권한만 (개발자도구로 SUPER 를 보내도 거부)
            throw new IllegalArgumentException("허용되지 않는 권한입니다 : " + role);
        }
        Member member = memberRepository.findById(id) // ② 없는 번호 → 예외
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        // [6주차 연습문제 ①] 관리자 본인 보호 : 대상이 로그인한 본인이면 거부
        member.setRole(role);
        return memberRepository.save(member); // ③ id 가 있으면 UPDATE
    }

    @PreAuthorize("hasRole('ADMIN')") // 관리자만 실행 가능 (2차 잠금)
    public void deleteMember(Long id) { // 회원 삭제
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        // [6주차 연습문제 ①] 관리자 본인 보호 : 대상이 로그인한 본인이면 거부
        memberRepository.delete(member); // DELETE
    }

    private boolean isBlank(String value) { // [7주차] null 또는 공백뿐인 값
        return value == null || value.isBlank();
    }
}
