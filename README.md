# 자바웹프로그래밍(2) 개인 실습 포트폴리오

Spring Boot 기반 개인 포트폴리오 웹 사이트 — 주차별 실습 결과를 계속 업데이트합니다.

## 개발 환경
| 항목 | 버전 |
|---|---|
| Spring Boot | 4.1.2 (Maven, Jar) |
| Java | 25 |
| View | Thymeleaf |
| DB | MySQL 8.0 + Spring Data JPA |
| IDE | VS Code (Extension Pack for Java, Spring Boot Extension Pack) |

## 실행 방법
1. MySQL 에 `spring` 데이터베이스 생성 : `create database spring;`
2. `src/main/resources/application-secret.properties` 파일을 직접 만든다. (GitHub 에는 올라가지 않음)
   ```properties
   spring.datasource.username=root
   spring.datasource.password=본인_비밀번호
   ```
3. `DemoApplication.java` 실행 (Ctrl + F5) → http://localhost:8080

## 주차별 진행 내역
- [x] 2주차 : 개발 환경 설정 및 테스트 (프로젝트 생성)
- [x] 4주차 : MySQL 연결 확인 (HikariPool Start completed), DB 계정 정보 분리
- [x] 2주차 : index.html / hello 페이지, URL 매핑 + 연습문제 hello2 (값 5개 출력)
- [ ] 3주차 : 포트폴리오 프론트 (부트스트랩 템플릿)
- [ ] 4주차 : TestDB 엔티티 · 리포지토리 · 서비스 · 컨트롤러
- [ ] 5주차 : 로그인 / 로그아웃, 비밀번호 암호화 (BCrypt)
- [ ] 6주차 : 권한(ROLE) 접근 제어, 관리자 페이지
- [ ] 7주차 : 회원 관리 REST API (JSON)
- [ ] 9주차 : 게시판 CRUD + 본인 확인
- [ ] 10주차 : 페이징 · 검색 · 댓글
- [ ] 11주차 : 파일 업로드 · 신청 바구니 · 트랜잭션

## 문제 해결 기록
- MySQL root 비밀번호 분실 → `mysqld --init-file` 로 재설정 (관리자 권한 PowerShell 필요)
- 프로젝트 경로는 한글 · OneDrive 경로를 피해 `C:\SPRING_BOOT_학번` 에 생성

## 느낀 점
<!-- 주차별로 배운 점, 어려웠던 점을 직접 적어 보세요 -->
