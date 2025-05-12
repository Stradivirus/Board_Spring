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
    const [validation, setValidation] = useState({
        userId: "",
        nickname: "",
        email: "",
        passwordConfirm: "",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });

        if (name === "passwordConfirm") {
            setValidation((prev) => ({
                ...prev,
                passwordConfirm: value !== form.password ? "비밀번호가 일치하지 않습니다" : "",
            }));
        }
    };

    const handleBlur = async (e: React.FocusEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        if (name === "passwordConfirm") {
            setValidation((prev) => ({
                ...prev,
                passwordConfirm: value !== form.password ? "비밀번호가 일치하지 않습니다" : "",
            }));
            return;
        }

        if (mode !== "register") return;

        if (["userId", "nickname", "email"].includes(name)) {
            if (value.trim() === "") {
                setValidation((prev) => ({
                    ...prev,
                    [name]: "",
                }));
                return;
            }
            try {
                const res = await fetch(`${API_URLS.MEMBER_CHECK}/${name}?value=${value}`);
                if (res.ok) {
                    const isDuplicate = await res.json();
                    setValidation((prev) => ({
                        ...prev,
                        [name]: isDuplicate
                            ? <span style={{ color: "red" }}>이미 사용 중입니다</span>
                            : <span style={{ color: "green" }}>사용 가능합니다</span>,
                    }));
                } else {
                    setValidation((prev) => ({
                        ...prev,
                        [name]: "확인 실패",
                    }));
                }
            } catch {
                setValidation((prev) => ({
                    ...prev,
                    [name]: "오류 발생",
                }));
            }
        }
    };

    // 1초 대기 함수
    const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

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
                await delay(1000); // 1초 대기

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
                onBlur={handleBlur}
                required
            />
            {validation.userId && (
                <div className={`validation ${validation.userId === "사용 가능합니다" ? "valid" : "invalid"}`}>
                    {validation.userId}
                </div>
            )}
            {mode === "register" && (
                <input
                    className="modal-input"
                    name="nickname"
                    placeholder="닉네임"
                    value={form.nickname}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    required
                />
            )}
            {mode === "register" && validation.nickname && (
                <div className={`validation ${validation.nickname === "사용 가능합니다" ? "valid" : "invalid"}`}>
                    {validation.nickname}
                </div>
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
                        onChange={(e) => {
                            setPasswordConfirm(e.target.value);
                            handleChange(e);
                        }}
                        onBlur={handleBlur}
                        required
                        minLength={8}
                    />
                    {validation.passwordConfirm && (
                        <div className="validation invalid" style={{ color: "red" }}>
                            {validation.passwordConfirm}
                        </div>
                    )}
                    <input
                        className="modal-input"
                        name="email"
                        type="email"
                        placeholder="이메일"
                        value={form.email}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        required
                    />
                    {validation.email && (
                        <div className={`validation ${validation.email === "사용 가능합니다" ? "valid" : "invalid"}`}>
                            {validation.email}
                        </div>
                    )}
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
