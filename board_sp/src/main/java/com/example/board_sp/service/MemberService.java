package com.example.board_sp.service;

import com.example.board_sp.dto.*;
import com.example.board_sp.entity.Member;
import com.example.board_sp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse join(MemberJoinRequest request) {
        if (memberRepository.existsByUserId(request.getUserId()))
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        if (memberRepository.existsByNickname(request.getNickname()))
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        if (memberRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        if (request.getPassword().length() < 8)
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");

        Member member = Member.builder()
                .userId(request.getUserId())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .joinedAt(LocalDate.now())
                .build();

        Member saved = memberRepository.save(member);

        MemberResponse response = new MemberResponse();
        response.setId(saved.getId());
        response.setUserId(saved.getUserId());
        response.setNickname(saved.getNickname());
        response.setEmail(saved.getEmail());
        response.setJoinedAt(saved.getJoinedAt());
        return response;
    }

    public MemberResponse login(MemberLoginRequest request) {
        Member member = memberRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setUserId(member.getUserId());
        response.setNickname(member.getNickname());
        response.setEmail(member.getEmail());
        response.setJoinedAt(member.getJoinedAt());
        return response;
    }
}
