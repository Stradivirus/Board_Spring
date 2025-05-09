import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import PostPage from "./pages/PostPage";
import PostNewPage from "./pages/PostNewPage";
import PostForm from "./components/PostForm";
import AdminDashboard from "./pages/AdminDashboard";
import DeletedPostList from "./pages/DeletedPostList";
import DeletedSoftPostList from "./pages/DeletedSoftPostList";
import './styles/Board.css';

const App: React.FC = () => (
    <BrowserRouter>
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/posts/:id" element={<PostPage />} />
            <Route path="/posts/:id/edit" element={<PostForm isEdit={true} />} />
            <Route path="/new" element={<PostNewPage />} />
            <Route path="/admin" element={<AdminDashboard />} />
            <Route path="/admin/archive" element={<DeletedPostList />} /> {/* 아카이브 글 */}
            <Route path="/admin/deleted" element={<DeletedSoftPostList />} /> {/* 소프트 딜리트 글 */}
        </Routes>
    </BrowserRouter>
);

export default App;
