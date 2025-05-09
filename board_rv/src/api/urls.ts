const API_BASE = "http://localhost:8080/api";

export const API_URLS = {
    POSTS: `${API_BASE}/posts`,
    POST: (id: number) => `${API_BASE}/posts/${id}`,
    ADMIN_ARCHIVE: `${API_BASE}/admin/archive`,
    ADMIN_DELETED: `${API_BASE}/admin/deleted`,
};
