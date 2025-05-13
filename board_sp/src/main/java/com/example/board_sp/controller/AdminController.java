package com.example.board_sp.controller;

import com.example.board_sp.dto.AdminBoardResponse;
import com.example.board_sp.entity.Member;
import com.example.board_sp.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/deleted")
    public ResponseEntity<Page<AdminBoardResponse>> getSoftDeletedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getSoftDeletedPosts(PageRequest.of(page, size)));
    }

    @GetMapping("/archive")
    public ResponseEntity<Page<AdminBoardResponse>> getHardDeletedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getHardDeletedPosts(PageRequest.of(page, size)));
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(adminService.getAllMembers());
    }
}