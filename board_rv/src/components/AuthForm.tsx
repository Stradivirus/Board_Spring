import React, { useState } from "react";
import type { Member } from "../types/Member";
import { useAuth } from "../context/AuthContext";
import { API_URLS } from "../api/urls";
import "../styles/Modal.css";

interface AuthFormProps {
    mode: "login" | "register";
    onSuccess?: (member: Member) => void;
}

const AuthForm: React.FC<AuthFormProps> = ({ mode, onSuccess }) => {
    const { login } = useAuth();
    const [form, setForm] = useState({
        userId: "",
        nickname: "",
        password: "",
        email: "",
    });
    const [passwordConfirm, setPasswordConfirm] = useState("");
    const [result, setResult] = useState<string>("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (mode === "register" && form.password !== passwordConfirm) {
            setResult("비밀번호가 일치하지 않습니다.");
            return;
        }
        setResult("로딩 중...");
        try {
            const url =
                mode === "login"
                    ? API_URLS.MEMBER_LOGIN
                    : API_URLS.MEMBER_JOIN;
            const payload =
                mode === "login"
                    ? { userId: form.userId, password: form.password }
                    : form;
            const res = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
            if (res.ok) {
                const data = await res.json();
                setResult(`${mode === "login" ? "로그인" : "회원가입"} 성공!`);
                if (mode === "login") {
                    login(
                        data.token || "dummy-token",
                        { userId: data.userId, nickname: data.nickname }
                    );
                }
                if (onSuccess) onSuccess(data);
            } else {
                const error = await res.text();
                setResult(`${mode === "login" ? "로그인" : "회원가입"} 실패: ${error}`);
            }
        } catch (err) {
            setResult(
                `${mode === "login" ? "로그인" : "회원가입"} 오류: ${
                    (err as Error).message
                }`
            );
        }
    };

    return (
        <form className="modal-form" onSubmit={handleSubmit}>
            <h3 className="modal-message" style={{ marginBottom: 12 }}>
                {mode === "login" ? "로그인" : "회원가입"}
            </h3>
            <input
                className="modal-input"
                name="userId"
                placeholder="아이디"
                value={form.userId}
                onChange={handleChange}
                required
            />
            {mode === "register" && (
                <input
                    className="modal-input"
                    name="nickname"
                    placeholder="닉네임"
                    value={form.nickname}
                    onChange={handleChange}
                    required
                />
            )}
            <input
                className="modal-input"
                name="password"
                type="password"
                placeholder="비밀번호"
                value={form.password}
                onChange={handleChange}
                required
                minLength={8}
            />
            {mode === "register" && (
                <>
                    <input
                        className="modal-input"
                        name="passwordConfirm"
                        type="password"
                        placeholder="비밀번호 확인"
                        value={passwordConfirm}
                        onChange={e => setPasswordConfirm(e.target.value)}
                        required
                        minLength={8}
                    />
                    <input
                        className="modal-input"
                        name="email"
                        type="email"
                        placeholder="이메일"
                        value={form.email}
                        onChange={handleChange}
                        required
                    />
                </>
            )}
            <button className="board-btn" type="submit">
                {mode === "login" ? "로그인" : "회원가입"}
            </button>
            <div className="modal-result">{result}</div>
        </form>
    );
};

export default AuthForm;
