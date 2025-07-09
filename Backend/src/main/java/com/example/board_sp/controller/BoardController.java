package com.example.board_sp.controller;

import com.example.board_sp.dto.BoardCreateRequest;
import com.example.board_sp.dto.BoardResponse;
import com.example.board_sp.entity.Board;
import com.example.board_sp.service.BoardService;
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
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getPostDetail(@PathVariable Long id) {
        Board board = boardService.getPostByIdAndIncrementViewCount(id, LocalDate.now());
        return ResponseEntity.ok(boardService.toResponse(board));
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<BoardResponse> getPostForEdit(@PathVariable Long id) {
        Board board = boardService.getPostById(id);
        return ResponseEntity.ok(boardService.toResponse(board));
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createPost(@RequestBody BoardCreateRequest request) {
        Board board = boardService.createPostFromDto(request);
        return ResponseEntity.ok(boardService.toResponse(board));
    }

    @GetMapping
    public ResponseEntity<Page<BoardResponse>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Board> boards = boardService.getAllPosts(PageRequest.of(page, size));
        Page<BoardResponse> response = new PageImpl<>(
                boards.getContent().stream().map(boardService::toResponse).collect(Collectors.toList()),
                boards.getPageable(),
                boards.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updatePost(
            @PathVariable Long id,
            @RequestBody BoardCreateRequest request
    ) {
        Board board = boardService.updatePostFromDto(id, request);
        return ResponseEntity.ok(boardService.toResponse(board));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boardService.deletePost(id, LocalDate.now());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
    }
}