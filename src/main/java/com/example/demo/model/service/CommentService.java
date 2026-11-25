package com.example.demo.model.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.model.domain.Comment;
import com.example.demo.model.domain.Member;
import com.example.demo.model.domain.Post;
import com.example.demo.model.repository.CommentRepository;
import com.example.demo.model.repository.MemberRepository;

@Service // [10주차] 댓글 서비스
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostService postService; // 글 찾기는 9주차 PostService 를 그대로 쓴다 (없으면 404 까지 재사용)

    public List<Comment> findByPost(Post post) { // 이 글의 댓글 목록
        return commentRepository.findByPostOrderByIdAsc(post);
    }

    // [12주차 연습문제 ②] 댓글 알림 : 글쓴이에게 "OO님이 '…' 에 댓글을 남겼습니다." (내 글에 내 댓글은 생략)
    //   → 이벤트를 던져도 이 메서드엔 @Transactional 이 없어서 @TransactionalEventListener 가 실행되지 않는다 (함정)
    public Comment write(Long postId, String content, String username) { // 댓글 쓰기
        if (content == null || content.isBlank() || content.length() > 500) { // ① 검사
            throw new IllegalArgumentException("댓글은 1~500자로 입력하세요.");
        }
        Post post = postService.findById(postId); // ② 없는 글이면 404
        Member author = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Comment comment = new Comment();
        comment.setContent(content.trim());
        comment.setPost(post);     // 어느 글의 댓글인지 (주인 쪽에서 저장)
        comment.setAuthor(author); // 작성자는 화면에서 받지 않는다 → 로그인한 사람
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment); // ③ INSERT
    }

    public void delete(Long id) { // 댓글 삭제 (본인 또는 관리자)
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
        if (!isAdmin()) checkWriter(comment);
        commentRepository.delete(comment); // DELETE
    }

    private void checkWriter(Comment comment) { // 로그인한 사람이 댓글 작성자인지 → 아니면 403
        String loginUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getAuthor().getUsername().equals(loginUsername)) {
            throw new AccessDeniedException("본인이 쓴 댓글만 삭제할 수 있습니다.");
        }
    }

    private boolean isAdmin() { // 로그인한 사람이 관리자인지 (역할 목록에 ROLE_ADMIN 이 하나라도 있으면 true)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
