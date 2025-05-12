import React from "react";
import { API_URLS } from "../api/urls";
import AdminDeletedList from "../components/AdminDeletedList";

const DeletedPostList: React.FC = () => (
    <>
        <AdminDeletedList
            apiUrl={API_URLS.ADMIN_ARCHIVE}
            title="아카이브 삭제글 목록"
        />
    </>
);

export default DeletedPostList;