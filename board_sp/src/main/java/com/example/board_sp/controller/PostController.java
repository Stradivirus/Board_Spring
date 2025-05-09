package com.example.board_sp.controller;

import com.example.board_sp.dto.PostCreateRequest;
import com.example.board_sp.dto.PostResponse;
import com.example.board_sp.entity.Board;
import com.example.board_sp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long id) {
        Board board = postService.getPostByIdAndIncrementViewCount(id, LocalDate.now());
        return ResponseEntity.ok(PostService.toResponse(board));
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<PostResponse> getPostForEdit(@PathVariable Long id) {
        Board board = postService.getPostById(id);
        return ResponseEntity.ok(PostService.toResponse(board));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        Board board = postService.createPostFromDto(request);
        return ResponseEntity.ok(PostService.toResponse(board));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Board> boards = postService.getAllPosts(PageRequest.of(page, size));
        Page<PostResponse> response = new PageImpl<>(
                boards.getContent().stream().map(PostService::toResponse).collect(Collectors.toList()),
                boards.getPageable(),
                boards.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody PostCreateRequest request
    ) {
        Board board = postService.updatePostFromDto(id, request);
        return ResponseEntity.ok(PostService.toResponse(board));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id, LocalDate.now());
        return ResponseEntity.noContent().build();
    }

    // 중복 등록 등 예외 발생 시 400 에러와 메시지 반환
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
    }
}