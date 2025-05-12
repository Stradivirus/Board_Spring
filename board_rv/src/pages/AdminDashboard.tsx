import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/admin.css";

const AdminDashboard: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div className="admin-container">
            <div className="admin-title-bar">
                <h2>관리자 대시보드</h2>
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: "24px", alignItems: "flex-start" }}>
                <button
                    className="admin-btn"
                    style={{ minWidth: 180 }}
                    onClick={() => navigate("/admin/deleted")}
                    title="운영 테이블 삭제글"
                >
                    운영 테이블 삭제글
                </button>
                <button
                    className="admin-btn"
                    style={{ minWidth: 180 }}
                    onClick={() => navigate("/admin/archive")}
                    title="아카이브 삭제글"
                >
                    아카이브 삭제글
                </button>
                <button
                    className="admin-btn"
                    style={{ minWidth: 180 }}
                    onClick={() => navigate("/admin/members")}
                    title="회원 목록"
                >
                    회원 목록
                </button>
            </div>
        </div>
    );
};

export default AdminDashboard;