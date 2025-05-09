package com.example.board_sp.service;

import com.example.board_sp.dto.PostCreateRequest;
import com.example.board_sp.dto.PostResponse;
import com.example.board_sp.entity.Board;
import com.example.board_sp.entity.BoardStatus;
import com.example.board_sp.entity.BoardArchive;
import com.example.board_sp.repository.PostRepository;
import com.example.board_sp.repository.PostArchiveRepository;
import com.example.board_sp.repository.BoardStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardStatusRepository boardStatusRepository;
    private final PostArchiveRepository postArchiveRepository;

    @Transactional
    public Board getPostByIdAndIncrementViewCount(Long id, LocalDate createdDate) {
        Board board = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        board.setViewCount(board.getViewCount() + 1);
        return postRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Board getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    }

    @Transactional
    public Board createPostFromDto(PostCreateRequest request) {
        Board board = new Board();
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setWriter(request.getWriter());
        board.setViewCount(0);
        board.setCreatedDate(LocalDate.now());
        board.setCreatedTime(LocalTime.now());
        Board savedBoard = postRepository.save(board);

        BoardStatus status = new BoardStatus();
        status.setBoardId(savedBoard.getId());
        status.setCreatedDate(savedBoard.getCreatedDate());
        status.setRevision(0);
        status.setDeleted(false);
        boardStatusRepository.save(status);

        return savedBoard;
    }

    @Transactional(readOnly = true)
    public Page<Board> getAllPosts(Pageable pageable) {
        return postRepository.findAllActive(pageable);
    }

    @Transactional
    public Board updatePostFromDto(Long id, PostCreateRequest request) {
        Board board = getPostById(id);
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setWriter(request.getWriter());
        Board updatedBoard = postRepository.save(board);

        int latestRevision = boardStatusRepository.findMaxRevision(board.getId(), board.getCreatedDate());
        BoardStatus status = new BoardStatus();
        status.setBoardId(board.getId());
        status.setCreatedDate(board.getCreatedDate());
        status.setRevision(latestRevision + 1);
        status.setDeleted(false);
        status.setUpdatedDate(LocalDate.now());
        status.setUpdatedTime(LocalTime.now());
        boardStatusRepository.save(status);

        return updatedBoard;
    }

    @Transactional
    public void deletePost(Long id, LocalDate createdDate) {
        Board board = getPostById(id);

        int latestRevision = boardStatusRepository.findMaxRevision(board.getId(), board.getCreatedDate());
        BoardStatus status = new BoardStatus();
        status.setBoardId(board.getId());
        status.setCreatedDate(board.getCreatedDate());
        status.setRevision(latestRevision + 1);
        status.setDeleted(true);
        status.setDeletedDate(LocalDate.now());
        status.setDeletedTime(LocalTime.now());
        boardStatusRepository.save(status);
    }

    // 소프트 삭제(아직 아카이브 안 된 글) 조회
    @Transactional(readOnly = true)
    public Page<Board> getSoftDeletedPosts(Pageable pageable) {
        return postRepository.findSoftDeleted(pageable);
    }

    // 하드 삭제(아카이브) 조회
    @Transactional(readOnly = true)
    public Page<BoardArchive> getHardDeletedPosts(Pageable pageable) {
        return postArchiveRepository.findAllDeleted(pageable);
    }

    // 소프트+하드 삭제 전체 조회 (페이징은 메모리에서 처리)
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllDeletedPosts(Pageable pageable) {
        Page<Board> softDeleted = getSoftDeletedPosts(pageable);
        Page<BoardArchive> hardDeleted = getHardDeletedPosts(pageable);

        List<PostResponse> merged = new ArrayList<>();
        softDeleted.getContent().forEach(b -> merged.add(toResponse(b, true)));
        hardDeleted.getContent().forEach(a -> merged.add(toResponse(a)));

        merged.sort(Comparator.comparing(PostResponse::getDeletedDate, Comparator.nullsLast(Comparator.reverseOrder())));

        return new PageImpl<>(merged, pageable, softDeleted.getTotalElements() + hardDeleted.getTotalElements());
    }

    // Entity -> DTO 변환 (Board)
    public static PostResponse toResponse(Board board) {
        return toResponse(board, false);
    }

    public static PostResponse toResponse(Board board, boolean deleted) {
        if (board == null) return null;
        PostResponse dto = new PostResponse();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setWriter(board.getWriter());
        dto.setViewCount(board.getViewCount());
        dto.setCreatedDate(board.getCreatedDate());
        dto.setCreatedTime(board.getCreatedTime());
        dto.setDeleted(deleted);
        // deletedDate, deletedTime 등은 필요시 BoardStatus에서 추가 조회
        return dto;
    }

    // Entity -> DTO 변환 (BoardArchive)
    public static PostResponse toResponse(BoardArchive post) {
        if (post == null) return null;
        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setWriter(post.getWriter());
        dto.setViewCount(post.getViewCount());
        dto.setCreatedDate(post.getCreatedDate());
        dto.setCreatedTime(post.getCreatedTime());
        dto.setDeleted(true);
        dto.setDeletedDate(post.getDeletedDate());
        return dto;
    }
}