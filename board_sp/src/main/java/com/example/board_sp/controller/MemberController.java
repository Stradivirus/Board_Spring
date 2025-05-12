package com.example.board_sp.controller;

import com.example.board_sp.dto.*;
import com.example.board_sp.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public MemberResponse join(@RequestBody MemberJoinRequest request) {
        return memberService.join(request);
    }

    @PostMapping("/login")
    public MemberResponse login(@RequestBody MemberLoginRequest request) {
        return memberService.login(request);
    }
}
