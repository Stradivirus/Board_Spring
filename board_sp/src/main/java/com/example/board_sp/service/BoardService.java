package com.example.board_sp.service;

import com.example.board_sp.dto.BoardCreateRequest;
import com.example.board_sp.dto.BoardResponse;
import com.example.board_sp.entity.Board;
import com.example.board_sp.entity.BoardStatus;
import com.example.board_sp.entity.BoardArchive;
import com.example.board_sp.entity.Member;
import com.example.board_sp.repository.BoardRepository;
import com.example.board_sp.repository.BoardArchiveRepository;
import com.example.board_sp.repository.BoardStatusRepository;
import com.example.board_sp.repository.MemberRepository;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardStatusRepository boardStatusRepository;
    private final BoardArchiveRepository boardArchiveRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Board getPostByIdAndIncrementViewCount(Long id, LocalDate createdDate) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        board.setViewCount(board.getViewCount() + 1);
        return boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Board getPostById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    }

    @Transactional
    public Board createPostFromDto(BoardCreateRequest request) {
        Member member = memberRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        boolean exists = boardRepository.existsByTitleAndWriterIdAndContentAndCreatedDate(
                request.getTitle(),
                member.getId(),
                request.getContent(),
                LocalDate.now()
        );
        if (exists) {
            throw new IllegalStateException("동일한 내용의 글이 이미 등록되어 있습니다.");
        }

        Board board = new Board();
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setWriterId(member.getId());
        board.setViewCount(0);
        board.setCreatedDate(LocalDate.now());
        board.setCreatedTime(LocalTime.now());
        Board savedBoard = boardRepository.save(board);

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
        return boardRepository.findAllActive(pageable);
    }

    @Transactional
    public Board updatePostFromDto(Long id, BoardCreateRequest request) {
        Board board = getPostById(id);

        Member member = memberRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setWriterId(member.getId());
        Board updatedBoard = boardRepository.save(board);

        // revision 및 이력 저장 로직 제거

        return updatedBoard;
    }

    @Transactional
    public void deletePost(Long id, LocalDate createdDate) {
        Board board = getPostById(id);

        // revision 및 이력 저장 로직 제거
        // 대신, 논리 삭제만 처리 (예: deleted 플래그만 true로 변경)
        // 아래는 예시로, 실제 논리 삭제 방식에 맞게 구현 필요
        BoardStatus status = new BoardStatus();
        status.setBoardId(board.getId());
        status.setCreatedDate(board.getCreatedDate());
        status.setRevision(0); // revision 관리 안함
        status.setDeleted(true);
        status.setDeletedDate(LocalDate.now());
        status.setDeletedTime(LocalTime.now());
        boardStatusRepository.save(status);
    }

    @Transactional(readOnly = true)
    public Page<Board> getSoftDeletedPosts(Pageable pageable) {
        return boardRepository.findSoftDeleted(pageable);
    }

    @Transactional(readOnly = true)
    public Page<BoardArchive> getHardDeletedPosts(Pageable pageable) {
        return boardArchiveRepository.findAllDeleted(pageable);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse> getAllDeletedPosts(Pageable pageable) {
        Page<Board> softDeleted = getSoftDeletedPosts(pageable);
        Page<BoardArchive> hardDeleted = getHardDeletedPosts(pageable);

        List<BoardResponse> merged = new ArrayList<>();
        softDeleted.getContent().forEach(b -> merged.add(toResponse(b, true)));
        hardDeleted.getContent().forEach(a -> merged.add(toResponse(a)));

        merged.sort(Comparator.comparing(BoardResponse::getDeletedDate, Comparator.nullsLast(Comparator.reverseOrder())));

        return new PageImpl<>(merged, pageable, softDeleted.getTotalElements() + hardDeleted.getTotalElements());
    }

    // Entity -> DTO 변환 (Board)
    public BoardResponse toResponse(Board board) {
        return toResponse(board, false);
    }

    public BoardResponse toResponse(Board board, boolean deleted) {
        if (board == null) return null;
        BoardResponse dto = new BoardResponse();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setWriterId(board.getWriterId());
        // 닉네임 세팅
        Member member = null;
        if (board.getWriterId() != null) {
            member = memberRepository.findById(board.getWriterId()).orElse(null);
        }
        dto.setWriterNickname(member != null ? member.getNickname() : "");
        dto.setViewCount(board.getViewCount());
        dto.setCreatedDate(board.getCreatedDate());
        dto.setCreatedTime(board.getCreatedTime());
        dto.setDeleted(deleted);
        // deletedDate, deletedTime 등은 필요시 BoardStatus에서 추가 조회
        return dto;
    }

    // Entity -> DTO 변환 (BoardArchive)
    public BoardResponse toResponse(BoardArchive post) {
        if (post == null) return null;
        BoardResponse dto = new BoardResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setWriterId(post.getWriterId());
        // 닉네임 세팅
        Member member = null;
        if (post.getWriterId() != null) {
            member = memberRepository.findById(post.getWriterId()).orElse(null);
        }
        dto.setWriterNickname(member != null ? member.getNickname() : "");
        dto.setViewCount(post.getViewCount());
        dto.setCreatedDate(post.getCreatedDate());
        dto.setCreatedTime(post.getCreatedTime());
        dto.setDeleted(true);
        dto.setDeletedDate(post.getDeletedDate());
        return dto;
    }
}