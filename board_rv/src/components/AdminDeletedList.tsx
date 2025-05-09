// AdminDeletedList.tsx
import React, { useEffect, useState } from "react";
import { formatDate } from "../utils/formatDate";
import { Link } from "react-router-dom";
import type { Post } from "../types/Post";
import "../styles/admin.css";

interface Props {
    apiUrl: string;
    title: string;
}

const PAGE_SIZE = 20;

const AdminDeletedList: React.FC<Props> = ({ apiUrl, title }) => {
    const [posts, setPosts] = useState<Post[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [selectedDate, setSelectedDate] = useState<string>("");

    useEffect(() => {
        setIsLoading(true);
        setError(null);
        const dateQuery = selectedDate ? `&date=${selectedDate}` : "";
        fetch(`${apiUrl}?page=${page}&size=${PAGE_SIZE}${dateQuery}`)
            .then(res => {
                if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
                return res.json();
            })
            .then(data => {
                const converted = (data.content || []).map((post: any) => ({
                    ...post,
                    createdDate: post.createdDate || post.created_date,
                    createdTime: post.createdTime || post.created_time,
                    deletedDate: post.deletedDate || post.deleted_date,
                    deletedTime: post.deletedTime || post.deleted_time,
                }));
                setPosts(converted);
                setTotalPages(data.totalPages ?? 1);
            })
            .catch(err => {
                setError(err.message || "데이터를 불러오는 중 오류가 발생했습니다.");
                setPosts([]);
            })
            .finally(() => setIsLoading(false));
    }, [apiUrl, page, selectedDate]);

    useEffect(() => {
        setPage(0);
    }, [selectedDate]);

    return (
        <div className="admin-container">
            <div className="admin-title-bar">
                <h2>{title}</h2>
                <input
                    type="date"
                    value={selectedDate}
                    onChange={e => setSelectedDate(e.target.value)}
                    style={{ marginLeft: 16 }}
                />
            </div>
            {isLoading && <div style={{ textAlign: "center", color: "#888", margin: "20px 0" }}>불러오는 중...</div>}
            {error && <div style={{ textAlign: "center", color: "red", margin: "20px 0" }}>⚠️ {error}</div>}
            {!isLoading && !error && (
                <>
                    <table className="admin-table">
                        <thead>
                        <tr>
                            <th style={{ width: "10%" }}>작성자</th>
                            <th style={{ width: "13%" }}>삭제날짜</th>
                            <th style={{ width: "13%" }}>삭제시간</th>
                            <th style={{ width: "13%" }}>작성날짜</th>
                            <th style={{ width: "13%" }}>작성시간</th>
                            <th style={{ width: "18%" }}>제목</th>
                            <th style={{ width: "20%" }}>내용</th>
                        </tr>
                        </thead>
                        <tbody>
                        {posts.length === 0 ? (
                            <tr>
                                <td colSpan={7} style={{ textAlign: 'center', color: '#aaa' }}>삭제된 글이 없습니다.</td>
                            </tr>
                        ) : posts.map((post, idx) => {
                            const { date: createdDate, time: createdTime } = formatDate(post.createdDate, post.createdTime);
                            const { date: deletedDate, time: deletedTime } = formatDate(post.deletedDate, post.deletedTime);
                            return (
                                <tr key={`${post.id}-${idx}`}>
                                    <td>{post.writer}</td>
                                    <td>{deletedDate}</td>
                                    <td>{deletedTime}</td>
                                    <td>{createdDate}</td>
                                    <td>{createdTime}</td>
                                    <td>{post.title}</td>
                                    <td style={{ whiteSpace: "pre-line", color: "#444" }}>{post.content}</td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </table>
                    <div style={{ display: "flex", justifyContent: "center", gap: "16px", marginTop: "18px" }}>
                        <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>이전</button>
                        <span>페이지 {page + 1} / {totalPages}</span>
                        <button onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page + 1 >= totalPages}>다음</button>
                    </div>
                </>
            )}
            <div style={{ marginTop: "24px" }}>
                <Link to="/admin" className="admin-btn">관리자 대시보드로</Link>
            </div>
        </div>
    );
};

export default AdminDeletedList;