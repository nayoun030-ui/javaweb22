package com.example.demo.controller;

import java.util.Set;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // [11주차] 모든 화면 컨트롤러에 공통으로 값을 넘긴다
public class CartBadgeAdvice {

    @ModelAttribute("cartCount") // 화면 상단 "바구니" 배지 숫자 = 세션 바구니의 모집글 수
    public int cartCount(HttpSession session) {
        Object cart = session.getAttribute("cart");
        return cart instanceof Set<?> set ? set.size() : 0;
    }
}
