export interface Post {
    deletedTime: string;
    deletedDate: string;
    id: number;
    title: string;
    writerId: number;
    writerNickname: string; // 추가
    content: string;
    createdDate: string;
    createdTime: string;
    viewCount: number;
}