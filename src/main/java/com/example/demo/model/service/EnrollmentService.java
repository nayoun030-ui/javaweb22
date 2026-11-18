package com.example.demo.model.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.model.domain.Enrollment;
import com.example.demo.model.domain.Member;
import com.example.demo.model.domain.Post;
import com.example.demo.model.repository.EnrollmentRepository;
import com.example.demo.model.repository.MemberRepository;
import com.example.demo.model.repository.PostRepository;

@Service // [11주차] 단계 4 : 신청 서비스 — 여러 테이블을 한 번에 다루는 일은 서비스가 맡는다
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    public List<Enrollment> findMine(String username) { // 내 신청 내역
        return enrollmentRepository.findByMember_UsernameOrderByIdDesc(username);
    }

    // 바구니의 모집글을 한 번에 신청 확정 : 모집글마다 ① 검사(SELECT) ② 신청 내역 INSERT ③ 신청 인원 UPDATE
    @Transactional // 단계 5 : 하나라도 실패하면 모두 취소 (롤백) — 없으면 앞의 save() 들이 이미 커밋되어 "일부만 신청"
    public int confirm(String username, Collection<Long> postIds) {
        if (postIds.isEmpty()) {
            throw new IllegalStateException("바구니가 비어 있습니다.");
        }
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));
        for (Long postId : postIds) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalStateException("삭제된 모집글이 바구니에 있습니다."));
            if (enrollmentRepository.existsByMemberAndPost(member, post)) {
                throw new IllegalStateException("이미 신청한 모집글입니다 : " + post.getTitle());
            }
            if (post.isFull()) { // 담을 때는 자리가 있었지만, 그사이 마감됐을 수 있다
                throw new IllegalStateException("정원이 마감되었습니다 : " + post.getTitle());
            }
            Enrollment enrollment = new Enrollment();
            enrollment.setMember(member);
            enrollment.setPost(post);
            enrollment.setCreatedAt(LocalDateTime.now());
            enrollmentRepository.save(enrollment); // ① INSERT 신청 내역

            post.setAppliedCount(post.getAppliedCount() + 1);
            postRepository.save(post); // ② UPDATE 신청 인원 + 1 (트랜잭션 안에서는 없어도 변경 감지로 UPDATE)
        }
        return postIds.size();
    }

    // [11주차 연습문제 ①] 신청 취소 : 본인 신청만, 두 작업을 한 트랜잭션으로, save() 없이 변경 감지로 인원 - 1
}
