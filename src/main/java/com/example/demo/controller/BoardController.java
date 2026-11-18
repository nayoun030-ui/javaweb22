package com.example.demo.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.domain.Post;
import com.example.demo.model.dto.PostForm;
import com.example.demo.model.service.CommentService;
import com.example.demo.model.service.PostService;

@Controller // 게시판 화면 컨트롤러
@RequestMapping("/board") // 모든 URL 앞에 /board
public class BoardController {

    @Autowired
    PostService postService;

    @Autowired // [10주차] 단계 3 : 상세 화면에 댓글 목록
    CommentService commentService;

    // [10주차] 목록 : GET /board?page=0&keyword=스프링 (누구나)
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,       // 없으면 "0" (숫자는 null 불가)
                       @RequestParam(required = false) String keyword,  // 없으면 null (오류 아님)
                       Model model) {
        Page<Post> posts = postService.findPage(keyword, page); // 페이지 단위로 10개씩
        model.addAttribute("posts", posts.getContent()); // 이번 페이지의 글 목록
        model.addAttribute("page", posts);               // 페이지 정보 → 화면에 번호가 생긴다
        model.addAttribute("keyword", keyword);          // 검색창 유지 + 페이지 링크에도 붙인다 (HTTP 는 무상태)
        return "board/list";
    }

    // [9주차 연습문제 ②] 내가 쓴 글 : GET /board/my → 목록 화면 재사용, listTitle 로 제목 변경

    @GetMapping("/{id}") // 상세 : GET /board/3 (누구나)
    public String detail(@PathVariable Long id, Model model) {
        Post post = postService.findById(id); // 없는 글 → 404 화면 (연습문제를 했다면 read(id))
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.findByPost(post)); // [10주차] 댓글 목록 → 댓글 영역
        return "board/detail";
    }

    @GetMapping("/write") // 글쓰기 화면 : GET /board/write (로그인 필요)
    public String writeForm() {
        return "board/form";
    }

    @PostMapping("/write") // 글쓰기 처리 : POST /board/write
    public String write(PostForm postForm, Principal principal, Model model) {
        try {
            Post post = postService.write(postForm, principal.getName()); // 작성자 = 로그인한 사람
            return "redirect:/board/" + post.getId(); // 저장 후 상세 화면으로
        } catch (IllegalArgumentException e) { // 빈 제목·내용
            model.addAttribute("error", e.getMessage());
            model.addAttribute("postForm", postForm);
            return "board/form"; // 입력값 유지한 채 다시 글쓰기 화면
        }
    }

    @GetMapping("/{id}/edit") // 수정 화면 : GET /board/3/edit
    public String editForm(@PathVariable Long id, Model model) {
        Post post = postService.findForEdit(id); // 본인 글이 아니면 403
        PostForm postForm = new PostForm(); // 기존 내용을 폼에 채워서 보여 준다
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        model.addAttribute("postForm", postForm);
        model.addAttribute("postId", id); // 화면이 "수정" 모드로 바뀐다
        return "board/form";
    }

    @PostMapping("/{id}/edit") // 수정 처리 : POST /board/3/edit
    public String edit(@PathVariable Long id, PostForm postForm, Model model) {
        try {
            postService.edit(id, postForm);
            return "redirect:/board/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("postForm", postForm);
            model.addAttribute("postId", id);
            return "board/form";
        }
    }

    @PostMapping("/{id}/delete") // 삭제 처리 : POST /board/3/delete
    public String delete(@PathVariable Long id) {
        postService.delete(id); // 본인 또는 관리자만
        return "redirect:/board?deleted"; // 목록 + "삭제되었습니다"
    }
}
