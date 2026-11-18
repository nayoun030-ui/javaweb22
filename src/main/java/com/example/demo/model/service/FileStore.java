package com.example.demo.model.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * [11주차] 업로드 파일 저장소 — 업로드 보안 4원칙 (OWASP 권고)
 * ① 확장자 허용 목록  ② 파일 앞부분(매직 넘버)으로 진짜 이미지인지  ③ 저장 이름은 서버가 새로(UUID)  ④ 웹 폴더(static)가 아닌 별도 폴더
 */
@Component
public class FileStore {

    private static final Set<String> ALLOWED = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private final Path root = Paths.get("uploads"); // 프로젝트 폴더/uploads/ (.gitignore 로 제외)

    public String save(MultipartFile file) { // 저장한 파일 이름을 돌려준다 (파일이 없으면 null)
        if (file == null || file.isEmpty()) return null; // 포스터 없는 글

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (ext == null || !ALLOWED.contains(ext.toLowerCase())) { // ① 허용한 확장자만 (.jsp · .html 차단)
            throw new IllegalArgumentException("이미지 파일(jpg, png, gif, webp)만 올릴 수 있습니다.");
        }
        try {
            Files.createDirectories(root);
            try (InputStream in = file.getInputStream()) {
                byte[] head = in.readNBytes(12);
                if (!isImage(head)) { // ② 이름만 .png 인 가짜 파일 차단 (확장자 · Content-Type 은 바꿀 수 있다)
                    throw new IllegalArgumentException("이미지 파일이 아닙니다.");
                }
                String storedName = UUID.randomUUID() + "." + ext.toLowerCase(); // ③ 같은 이름 덮어쓰기 · ../ 경로 공격 방지
                try (InputStream all = file.getInputStream()) {
                    Files.copy(all, root.resolve(storedName)); // ④ uploads/ 에 저장
                }
                return storedName; // DB 에는 이름만
            }
        } catch (IOException e) {
            throw new UncheckedIOException("파일을 저장하지 못했습니다.", e);
        }
    }

    public void delete(String storedName) { // [11주차 연습문제 ②] 글을 지우면 포스터 파일도 삭제
        if (storedName == null) return;
        try {
            Files.deleteIfExists(root.resolve(storedName).normalize());
        } catch (IOException ignored) { }
    }

    private boolean isImage(byte[] b) { // 파일 첫 바이트(매직 넘버)로 형식 확인
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') return true; // PNG  89 50 4E 47
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) return true; // JPG  FF D8 FF
        if (b.length >= 4 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8') return true;       // GIF  47 49 46 38
        return b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';                             // WEBP RIFF....WEBP
    }
}
