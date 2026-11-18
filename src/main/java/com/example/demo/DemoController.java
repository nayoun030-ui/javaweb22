package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 컨트롤러 어노테이션 명시
public class DemoController {

   @GetMapping("/hello2")
public String hello2(Model model) {
    model.addAttribute("msg1", "안녕하세요.");
    model.addAttribute("msg2", "저는 김나연입니다.");
    model.addAttribute("msg3", "학번은 20250590입니다.");
    model.addAttribute("msg4", "스프링 부트를 배우고 있습니다.");
    model.addAttribute("msg5", "잘 부탁드립니다!");

    return "hello2"; // hello2.html 연결
}
    @GetMapping("/hello") // 전송 방식 GET
    public String hello(Model model) {
        model.addAttribute("data", "반갑습니다."); // model 설정
        return "hello"; // hello.html 연결
    }
}
