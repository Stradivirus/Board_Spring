import React from "react";
import {BrowserRouter, Routes, Route} from "react-router-dom";
import {AuthProvider} from "./context/AuthContext";
import BoardHeader from "./components/BoardHeader";
import Home from "./pages/Home";
import PostPage from "./pages/PostPage";
import PostNewPage from "./pages/PostNewPage";
import PostForm from "./components/PostForm";
import AdminDashboard from "./pages/AdminDashboard";
import DeletedPostList from "./pages/DeletedPostList";
import DeletedSoftPostList from "./pages/DeletedSoftPostList";
import AdminMembers from "./pages/AdminMembers";

const App: React.FC = () => (
    <AuthProvider>
        <BrowserRouter>
            <BoardHeader/>
            <main>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/posts/:id" element={<PostPage/>}/>
                    <Route path="/posts/:id/edit" element={<PostForm isEdit={true}/>}/>
                    <Route path="/new" element={<PostNewPage/>}/>
                    <Route path="/admin" element={<AdminDashboard/>}/>
                    <Route path="/admin/archive" element={<DeletedPostList/>}/>
                    <Route path="/admin/deleted" element={<DeletedSoftPostList/>}/>
                    <Route path="/admin/members" element={<AdminMembers/>}/>
                </Routes>
            </main>
        </BrowserRouter>
    </AuthProvider>
);

export default App;
