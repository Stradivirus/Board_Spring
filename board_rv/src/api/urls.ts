const API_BASE = "http://localhost:8081/api";

export const API_URLS = {
    POSTS: `${API_BASE}/posts`,
    POST: (id: number) => `${API_BASE}/posts/${id}`,
    ADMIN_ARCHIVE: `${API_BASE}/admin/archive`,
    ADMIN_DELETED: `${API_BASE}/admin/deleted`,
    ADMIN_MEMBERS: `${API_BASE}/admin/members`,
    MEMBER_LOGIN: `${API_BASE}/member/login`,
    MEMBER_JOIN: `${API_BASE}/member/join`,
    MEMBER_CHECK: `${API_BASE}/member/check`,
};