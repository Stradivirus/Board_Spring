import React, {useEffect, useState} from "react";
import type {Member} from "../types/Member";
import {API_URLS} from "../api/urls";
import "../styles/admin.css";

const AdminMembers: React.FC = () => {
    const [members, setMembers] = useState<Member[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch(API_URLS.ADMIN_MEMBERS)
            .then(res => res.json())
            .then(data => {
                setMembers(data);
                setLoading(false);
            });
    }, []);

    if (loading) return <div>로딩 중...</div>;

    return (
        <div className="admin-container">
            <div className="admin-title-bar">
                <h2>회원 목록</h2>
            </div>
            <table className="admin-table">
                <thead>
                <tr>
                    <th>회원 번호</th>
                    <th>아이디</th>
                    <th>닉네임</th>
                    <th>이메일</th>
                    <th>가입일</th>
                </tr>
                </thead>
                <tbody>
                {members.map(m => (
                    <tr key={m.id}>
                        <td>{m.id}</td>
                        <td>{m.userId}</td>
                        <td>{m.nickname}</td>
                        <td>{m.email}</td>
                        <td>{m.joinedAt}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default AdminMembers;