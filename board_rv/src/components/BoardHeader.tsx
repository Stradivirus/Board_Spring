import React from "react";
import "../styles/Board.css";

const BoardHeader: React.FC = () => (
    <header className="board-header board-header--mb">
        <h1 className="board-header__title">
            Spring Board
        </h1>
        <p className="board-header__desc">
            자유롭게 글을 작성하고 소통하는 공간입니다.
        </p>
    </header>
);

export default BoardHeader;