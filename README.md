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
- [x] 3주차 : 포트폴리오 프론트 (TemplateMo 578 First Portfolio, Bootstrap 5) 적용, 정적 자원 경로 Thymeleaf `@{...}` 변환
- [x] 3주차 : 내 정보로 내용 수정 (Hero · About · Information · Services 4개 · Contact), 상세 페이지 `public/detailed_web.html`
- [ ] 3주차 연습문제 : 나머지 서비스 3개(AI · 디자인 · 앱) 상세 페이지, 프로필 사진 교체
- [x] 4주차 : MVC 폴더 구조(controller · model/domain · repository · service), TestDB 엔티티 · 리포지토리 · 서비스, `/testdb` 회원 목록(th:each)
- [ ] 4주차 연습문제 : 나이 · 성별 컬럼 추가, INSERT 후 testdb.html 출력
- [x] 5주차 : Spring Security 로그인 / 로그아웃, 회원가입, 비밀번호 암호화 (BCrypt), 메뉴 로그인 상태 표시 (sec:authorize)
- [ ] 5주차 연습문제 : ① 로그인 상태 유지 (remember-me) ② 비밀번호 확인 검증
- [x] 6주차 : 권한(ROLE) 접근 제어 3단계 (화면 sec:authorize · URL hasRole · 메서드 @PreAuthorize), 내 정보 `/mypage`, 관리자 회원 관리 `/admin/members`, 403 화면
- [ ] 6주차 연습문제 : ① 관리자 본인 보호 ② MANAGER 권한 추가 (/testdb 는 ADMIN · MANAGER 만)
- [x] 7주차 : 회원 관리 REST API (JSON) — `@RestController`, 응답 DTO(record), Basic 인증, 상태 코드(200·201·204·400·401·403·404), fetch 화면 `/admin/members-api`
- [ ] 7주차 연습문제 : ① 회원 검색 API (`?keyword=`) ② `@RestControllerAdvice` 로 오류 처리 모으기

### 7주차 API 테스트 결과 (Postman / curl, Basic 인증)
| 요청 | 계정 | 결과 |
|---|---|---|
| `GET /api/hello` | 없음 | 200 `{"message":"안녕하세요, REST API!", ...}` |
| `GET /api/me` | 없음 / student2 / 틀린 비밀번호 | 401 / 200 (password 없음) / 401 |
| `GET /api/admin/members` | student2 / admin | 403 / 200 |
| `GET /api/admin/members/9999` | admin | 404 |
| `POST /api/admin/members` (JSON) | admin | 201 + `Location`, 같은 아이디 400, 빈 값 400, 폼 전송 415, `"role":"ADMIN"` 몰래 넣어도 USER |
| `PATCH /api/admin/members/{id}/role` | admin | `{"role":"ADMIN"}` 200, `{"role":"SUPER"}` 400, PUT 405 |
| `DELETE /api/admin/members/{id}` | student2 / admin | 403 / 204 → 다시 조회 404 |
- [x] 9주차 : 게시판 CRUD (`Post` @ManyToOne 작성자, 글쓰기·목록·상세·수정·삭제), 본인 확인(남의 글 수정·삭제 403, 관리자 삭제), 없는 글 404
- [ ] 9주차 연습문제 : ① 조회수 ② 내가 쓴 글 모아 보기 (`/board/my`)

### 9주차 게시판 테스트 결과
| 계정 | 하는 일 | 결과 |
|---|---|---|
| 비로그인 | 목록 / 상세 / `/board/write` | 200 (글쓰기·수정 버튼 없음) / 200 / 로그인 화면 |
| student2 | 글쓰기, 빈 제목, 내 글 수정 | 상세로 이동 / "제목과 내용을 모두 입력하세요." / "수정" 시간 표시 |
| student3 | 남의 글 `/board/1/edit` 주소 입력, POST 수정·삭제 | 403 · 403 · 403 (제목 그대로) |
| admin | 남의 글 보기 / 수정 / 삭제 | "관리자 삭제" 버튼 / 403 (수정은 본인만) / 삭제 → 404 |
- [x] 10주차 : 페이징(`Pageable` · `Page`, 10개씩), 검색(JPQL `@Query` + 파라미터 바인딩), 댓글(`Comment` 글 · 작성자 N:1, 쓰기 · 삭제, PRG), 삭제 정리(글→댓글 cascade, 글·댓글 있는 회원 삭제 막기)
- [ ] 10주차 연습문제 : ① 내가 쓴 글 페이징 ② 작성자로 검색

### 10주차 테스트 결과 (테스트 글 105개)
| 구분 | 하는 일 | 결과 |
|---|---|---|
| 페이징 | `/board` · `?page=1` · 마지막 · `?page=-1` · `?page=999` | 10개 "1 / 11 페이지" · 다음 10개 · 남은 6개 · 첫 페이지 · 빈 목록 (오류 없음) |
| SQL | 목록 1페이지 | `... order by id desc limit ?,?` + `select count(...)` 2번 |
| 검색 | "스터디" · 2페이지 · "파이썬" · 공백 · `' OR 1=1 --` | 13개 2페이지 · keyword 유지 · "검색 결과가 없습니다." · 전체 · 0개 (바인딩) |
| 댓글 | 비로그인 · 쓰기 · 빈 댓글 · F5 | "로그인하면 댓글을 쓸 수 있습니다." · 오래된 순 · "1~500자" 안내 · 중복 등록 없음 (PRG) |
| 댓글 삭제 | 글 주인이 남의 댓글 · 본인 · 관리자 | 403 · 삭제 · 모든 댓글에 "관리자 삭제" |
| 삭제 정리 | 댓글 달린 글 삭제 · 댓글 쓴 회원 삭제 | `delete from comments` → `delete from posts` · 500 대신 안내 메시지 (API 400) |
- [ ] 11주차 : 파일 업로드 · 신청 바구니 · 트랜잭션

## 문제 해결 기록
- MySQL root 비밀번호 분실 → `mysqld --init-file` 로 재설정 (관리자 권한 PowerShell 필요)
- 프로젝트 경로는 한글 · OneDrive 경로를 피해 `C:\SPRING_BOOT_학번` 에 생성

## 느낀 점
<!-- 주차별로 배운 점, 어려웠던 점을 직접 적어 보세요 -->
