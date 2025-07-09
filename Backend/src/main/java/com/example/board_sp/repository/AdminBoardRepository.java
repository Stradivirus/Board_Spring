package com.example.board_sp.repository;

import com.example.board_sp.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminBoardRepository extends JpaRepository<Board, Long> {
}