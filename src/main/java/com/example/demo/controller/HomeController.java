package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.model.repository.EnrollmentRepository;
import com.example.demo.model.repository.PostRepository;
import lombok.RequiredArgsConstructor;

// 메인 화면 : 예전에는 templates/index.html 을 스프링이 "환영 페이지"로 그냥 보여 줬다.
// 숫자 4칸을 DB 값으로 채우려고 컨트롤러를 거치게 바꿨다 (→ CartBadgeAdvice 의 바구니 배지도 함께 들어온다).
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostRepository postRepository;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("postCount", postRepository.count());              // 전체 게시글
        model.addAttribute("openRecruitCount", postRepository.countOpenRecruits()); // 모집 중 특강·스터디
        model.addAttribute("enrollCount", enrollmentRepository.count());      // 누적 신청
        model.addAttribute("weekCount", 11);                                  // 주차별 실습 (진도에 맞춰 직접 올린다)
        return "index";
    }
}
