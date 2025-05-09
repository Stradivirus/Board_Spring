package com.example.board_sp.controller;

import com.example.board_sp.dto.PostResponse;
import com.example.board_sp.entity.Board;
import com.example.board_sp.entity.BoardArchive;
import com.example.board_sp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PostService postService;

    // 소프트 삭제(아직 아카이브 안 된 글)만 조회
    @GetMapping("/deleted")
    public ResponseEntity<Page<PostResponse>> getSoftDeletedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Board> boards = postService.getSoftDeletedPosts(PageRequest.of(page, size));
        Page<PostResponse> response = new PageImpl<>(
                boards.getContent().stream()
                        .map(b -> PostService.toResponse(b, true))
                        .collect(Collectors.toList()),
                boards.getPageable(),
                boards.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    // 하드 삭제(아카이브)만 조회
    @GetMapping("/archive")
    public ResponseEntity<Page<PostResponse>> getHardDeletedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BoardArchive> archives = postService.getHardDeletedPosts(PageRequest.of(page, size));
        Page<PostResponse> response = new PageImpl<>(
                archives.getContent().stream()
                        .map(PostService::toResponse)
                        .collect(Collectors.toList()),
                archives.getPageable(),
                archives.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }
}