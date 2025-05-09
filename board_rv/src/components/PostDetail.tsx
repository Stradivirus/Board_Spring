import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import type { Post } from "../types/Post";
import { API_URLS } from "../api/urls";
import { formatDate } from "../utils/formatDate";
import BoardHeader from "./BoardHeader";
import "../styles/Board.css";
import "../styles/modal.css";

const RecentPostList: React.FC<{ excludeId?: string }> = ({ excludeId }) => {
    const [posts, setPosts] = useState<Post[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        setError(null);
        fetch(`${API_URLS.POSTS}?page=0&size=6&sort=createdDate,desc`)
            .then(res => {
                if (!res.ok) throw new Error("최신 글을 불러오지 못했습니다.");
                return res.json();
            })
            .then(data => {
                const filtered = (data.content || []).filter((post: Post) => String(post.id) !== excludeId).slice(0, 5);
                setPosts(filtered);
            })
            .catch(err => setError(err.message || "최신 글을 불러오는 중 오류가 발생했습니다."));
    }, [excludeId]);

    if (error) return <div className="error-message">{error}</div>;
    if (!posts.length) return null;

    return (
        <section className="board-container" style={{ marginTop: 48 }}>
            <h3 style={{ fontSize: "1.3rem", fontWeight: 700, marginBottom: 18, color: "#2c3550" }}>
                최신 글 5개
            </h3>
            <table className="board-table">
                <thead>
                <tr>
                    <th style={{ width: "7%" }}>번호</th>
                    <th style={{ width: "45%" }}>제목</th>
                    <th>작성자</th>
                    <th>작성일</th>
                    <th>작성시간</th>
                    <th>조회수</th>
                </tr>
                </thead>
                <tbody>
                {posts.map(post => {
                    const { date, time } = formatDate(post.createdDate, post.createdTime);
                    return (
                        <tr key={post.id}>
                            <td>{post.id}</td>
                            <td>
                                <Link to={`/posts/${post.id}`} className="board-post-title-link">
                                    {post.title}
                                </Link>
                            </td>
                            <td className="board-post-author">{post.writer}</td>
                            <td className="board-post-date">{date}</td>
                            <td className="board-post-date">{time}</td>
                            <td className="board-post-views">{post.viewCount}</td>
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </section>
    );
};

const ConfirmModal: React.FC<{
    open: boolean;
    x: number;
    y: number;
    onConfirm: () => void;
    onCancel: () => void;
}> = ({ open, x, y, onConfirm, onCancel }) => {
    if (!open) return null;
    return (
        <div
            className="modal-follow"
            style={{
                position: "absolute",
                left: x,
                top: y,
                zIndex: 2000,
            }}
        >
            <button className="board-btn" onClick={onConfirm}>예</button>
            <button className="board-btn cancel" onClick={onCancel} style={{ marginLeft: 8 }}>아니오</button>
        </div>
    );
};

const PostDetail: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [post, setPost] = useState<Post | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [isDeleting, setIsDeleting] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [modalPos, setModalPos] = useState({ x: 0, y: 0 });
    const navigate = useNavigate();

    useEffect(() => {
        if (id) {
            setError(null);
            fetch(API_URLS.POST(Number(id)))
                .then(res => {
                    if (!res.ok) throw new Error("글 정보를 불러오지 못했습니다.");
                    return res.json();
                })
                .then(data => setPost(data))
                .catch(err => setError(err.message || "글 정보를 불러오지 못했습니다."));
        }
    }, [id]);

    const handleDelete = async () => {
        if (!id) return;
        setIsDeleting(true);
        setError(null);
        try {
            const res = await fetch(API_URLS.POST(Number(id)), { method: "DELETE" });
            if (!res.ok) throw new Error("삭제에 실패했습니다.");
            navigate("/");
        } catch (err: any) {
            setError(err?.message || "삭제 중 오류가 발생했습니다.");
        } finally {
            setIsDeleting(false);
        }
    };

    const handleDeleteClick = (e: React.MouseEvent) => {
        // 스크롤 위치까지 고려해서 모달 위치 지정
        setModalPos({
            x: e.clientX + window.scrollX,
            y: e.clientY + window.scrollY,
        });
        setShowConfirm(true);
    };

    if (error) {
        return (
            <>
                <BoardHeader />
                <div className="board-detail-container">
                    <div className="error-message">{error}</div>
                    <Link to="/" className="board-btn" style={{ marginTop: 24 }}>목록으로</Link>
                </div>
            </>
        );
    }

    if (!post) return (
        <>
            <BoardHeader />
            <div className="board-detail-container">로딩 중...</div>
        </>
    );

    const { date, time } = formatDate(post.createdDate, post.createdTime);

    return (
        <>
            <BoardHeader />
            <main className="board-detail-container board-detail-outer">
                <div className="board-detail-title board-detail-title-strong">
                    {post.title}
                </div>
                <div className="board-detail-meta board-detail-meta-flex">
                    <span><b>작성자</b> {post.writer}</span>
                    <span><b>작성일</b> {date}</span>
                    <span><b>작성시간</b> {time}</span>
                    <span><b>조회수</b> {post.viewCount}</span>
                </div>
                <div className="board-detail-content board-detail-content-bg">
                    {post.content}
                </div>
                <div className="board-detail-btn-group board-detail-btn-group-right">
                    <Link to={`/posts/${post.id}/edit`}>
                        <button className="board-btn">수정</button>
                    </Link>
                    <button
                        className="board-btn"
                        onClick={handleDeleteClick}
                        disabled={isDeleting}
                    >
                        {isDeleting ? "삭제 중..." : "삭제"}
                    </button>
                    <Link to="/" className="board-btn cancel">목록으로</Link>
                </div>
            </main>
            <ConfirmModal
                open={showConfirm}
                x={modalPos.x}
                y={modalPos.y}
                onConfirm={() => {
                    setShowConfirm(false);
                    handleDelete();
                }}
                onCancel={() => setShowConfirm(false)}
            />
            <RecentPostList excludeId={String(post.id)} />
        </>
    );
};

export default PostDetail;