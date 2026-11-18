package com.example.demo.controller; // [4주차] controller 폴더로 이동

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.model.domain.TestDB;
import com.example.demo.model.service.TestService; // 서비스 연결

@Controller // 컨트롤러 어노테이션 명시
public class DemoController {

    @Autowired
    TestService testService; // DemoController 클래스 안에 서비스 객체 주입

    @GetMapping("/hello2") // [2주차 연습문제]
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

    // [4주차] 데이터베이스 연동 테스트
    @GetMapping("/testdb")
    public String getAllTestDBs(Model model) {
        // 1단계 : 한 명 조회 (자료 실습) → 2단계 : 전체 조회로 변경
        // TestDB test = testService.findByName("홍길동");
        // model.addAttribute("data4", test);
        // System.out.println("데이터 출력 디버그 : " + test);
        List<TestDB> users = testService.findAll(); // SELECT * FROM testdb
        model.addAttribute("users", users);
        return "testdb"; // testdb.html 연결
    }
}
