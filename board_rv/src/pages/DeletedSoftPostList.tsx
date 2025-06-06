import React from "react";
import {API_URLS} from "../api/urls";
import AdminDeletedList from "../components/AdminDeletedList";

const DeletedSoftPostList: React.FC = () => (
    <>
        <AdminDeletedList
            apiUrl={API_URLS.ADMIN_DELETED}
            title="운영 테이블 삭제글 목록 (소프트 딜리트)"
        />
    </>
);

export default DeletedSoftPostList;