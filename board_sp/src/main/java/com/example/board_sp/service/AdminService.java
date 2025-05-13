package com.example.board_sp.service;

import com.example.board_sp.dto.AdminBoardResponse;
import com.example.board_sp.entity.Board;
import com.example.board_sp.entity.BoardArchive;
import com.example.board_sp.entity.BoardStatus;
import com.example.board_sp.entity.Member;
import com.example.board_sp.repository.AdminBoardRepository;
import com.example.board_sp.repository.BoardArchiveRepository;
import com.example.board_sp.repository.BoardStatusRepository;
import com.example.board_sp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminBoardRepository adminBoardRepository;
    private final BoardArchiveRepository boardArchiveRepository;
    private final BoardStatusRepository boardStatusRepository;
    private final MemberRepository memberRepository;

    // 논리삭제(soft delete) 게시글 목록
    public Page<AdminBoardResponse> getSoftDeletedPosts(Pageable pageable) {
        Page<BoardStatus> deletedStatuses = boardStatusRepository.findSoftDeletedLatest(pageable);
        List<AdminBoardResponse> dtos = deletedStatuses.stream()
                .map(this::toAdminBoardResponse)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, deletedStatuses.getTotalElements());
    }

    private AdminBoardResponse toAdminBoardResponse(BoardStatus status) {
        Board board = adminBoardRepository.findById(status.getBoardId()).orElse(null);
        if (board == null) return null;
        Member member = memberRepository.findById(board.getWriterId()).orElse(null);
        AdminBoardResponse dto = new AdminBoardResponse();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setWriterId(board.getWriterId());
        dto.setWriterNickname(member != null ? member.getNickname() : "");
        dto.setViewCount(board.getViewCount());
        dto.setCreatedDate(board.getCreatedDate());
        dto.setCreatedTime(board.getCreatedTime());
        dto.setDeletedDate(status.getDeletedDate());
        dto.setDeletedTime(status.getDeletedTime());
        return dto;
    }

    // 하드삭제(아카이브) 게시글 목록
    public Page<AdminBoardResponse> getHardDeletedPosts(Pageable pageable) {
        Page<BoardArchive> archives = boardArchiveRepository.findAll(pageable);
        return archives.map(archive -> {
            Member member = memberRepository.findById(archive.getWriterId()).orElse(null);
            AdminBoardResponse dto = new AdminBoardResponse();
            dto.setId(archive.getId());
            dto.setTitle(archive.getTitle());
            dto.setContent(archive.getContent());
            dto.setWriterId(archive.getWriterId());
            dto.setWriterNickname(member != null ? member.getNickname() : "");
            dto.setViewCount(archive.getViewCount());
            dto.setCreatedDate(archive.getCreatedDate());
            dto.setCreatedTime(archive.getCreatedTime());
            dto.setDeletedDate(archive.getDeletedDate());
            dto.setDeletedTime(archive.getDeletedTime());
            return dto;
        });
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}