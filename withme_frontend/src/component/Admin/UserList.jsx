import React, { useState, useEffect } from 'react';
import '../../assets/css/admin/DoctorList.css';
import { API_URL } from '../../constant';
import { fetchWithAuth } from '../../common/fetchWithAuth'; // fetchWithAuth import

export default function UserList() {
    const [users, setUsers] = useState([]);  // 전체 유저 리스트 상태
    const [filteredUsers, setFilteredUsers] = useState([]);  // 필터링된 유저 리스트 상태
    const [loading, setLoading] = useState(false);  // 로딩 상태
    const [error, setError] = useState(null);  // 에러 상태
    const [currentPage, setCurrentPage] = useState(0);  // 현재 페이지 (백엔드 기준 0부터 시작)
    const [pageSize] = useState(10);  // 페이지 당 아이템 개수

    // 전체 데이터에서 해당 페이지에 맞는 데이터만 추출
    const totalPages = Math.ceil(filteredUsers.length / pageSize);  // 전체 페이지 수 계산
    const currentPageData = filteredUsers.slice(currentPage * pageSize, (currentPage + 1) * pageSize);

    // ✅ 검색 쿼리 상태
    const [searchQuery, setSearchQuery] = useState({
        name: '',
        email: '',
        phone: '',
        role: ''
    });

    // ✅ 유저 리스트 가져오기 (페이징 없이 모든 데이터 가져오기)
    const fetchUsers = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetchWithAuth(`${API_URL}members/list`, {
                method: 'GET',
            });

            const data = await response.json();
            console.log("받아온 data : ", data);

            if (data) {
                setUsers(data);  // 전체 데이터를 상태에 저장
                setFilteredUsers(data);  // 필터링된 데이터도 초기화 (전체 데이터로 시작)
            } else {
                setUsers([]);
                setFilteredUsers([]);
            }
        } catch (err) {
            setError('사용자 데이터를 가져오는 데 실패했습니다.');
            console.error("API 요청 실패:", err);
        } finally {
            setLoading(false);
        }
    };

    // ✅ 페이지 변경 핸들러
    const handlePageChange = (page) => {
        if (page >= 0 && page < totalPages) {
            setCurrentPage(page);
        }
    };

    // ✅ 검색어 변경 시 필터링 처리
    const handleSearchChange = (e) => {
        const { name, value } = e.target;
        setSearchQuery(prev => ({ ...prev, [name]: value }));
    };

    // ✅ 필터링된 유저 리스트 업데이트 (검색 조건에 맞는 데이터만 필터링)
    useEffect(() => {
        let filtered = users;

        if (searchQuery.name) {
            filtered = filtered.filter(user => user.name.toLowerCase().includes(searchQuery.name.toLowerCase()));
        }
        if (searchQuery.email) {
            filtered = filtered.filter(user => user.email.toLowerCase().includes(searchQuery.email.toLowerCase()));
        }
        if (searchQuery.phone) {
            filtered = filtered.filter(user => user.phone.includes(searchQuery.phone));
        }
        if (searchQuery.role) {
            filtered = filtered.filter(user => user.role.toLowerCase().includes(searchQuery.role.toLowerCase()));
        }

        setFilteredUsers(filtered);  // 필터링된 유저 리스트 상태 업데이트
        setCurrentPage(0);  // 검색 후 첫 페이지로 리셋
    }, [searchQuery, users]);

    // ✅ 컴포넌트 마운트 시 데이터 가져오기
    useEffect(() => {
        fetchUsers();  // 데이터를 처음 불러옵니다.
    }, []);

    return (
        <div className="doctor-list-container">
            <h1 className="title">사용자 리스트</h1>

            {/* ✅ 검색 입력창 */}
            <div className="search-container">
                <input
                    type="text"
                    name="name"
                    placeholder="이름 검색"
                    value={searchQuery.name}
                    onChange={handleSearchChange}
                />
                <input
                    type="text"
                    name="email"
                    placeholder="이메일 검색"
                    value={searchQuery.email}
                    onChange={handleSearchChange}
                />
                <input
                    type="text"
                    name="phone"
                    placeholder="전화번호 검색"
                    value={searchQuery.phone}
                    onChange={handleSearchChange}
                />
                <input
                    type="text"
                    name="role"
                    placeholder="권한 검색"
                    value={searchQuery.role}
                    onChange={handleSearchChange}
                />
            </div>

            {/* 로딩 상태 처리 */}
            {loading ? (
                <p className="loading">데이터를 불러오는 중...</p>
            ) : (
                <>
                    <table className="doctor-table">
                        <thead>
                            <tr>
                                <th>이름</th>
                                <th>이메일</th>
                                <th>주소</th>
                                <th>전화번호</th>
                                <th>권한</th>
                                <th>포인트</th>
                                <th>가입일</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentPageData.length > 0 ? (
                                currentPageData.map((user) => (
                                    <tr key={user.id}>
                                        <td>{user.name}</td>
                                        <td>{user.email}</td>
                                        <td>{user.address}</td>
                                        <td>{user.phone}</td>
                                        <td>{user.role}</td>
                                        <td>{user.points}</td>
                                        <td>{new Date(user.regTime).toLocaleDateString('ko-KR')}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="7" style={{ textAlign: 'center' }}>
                                        검색 결과가 없습니다.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>

                    {/* 페이징 버튼 */}
                    <div className="pagination">
                        <button onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 0}>
                            이전
                        </button>
                        {Array.from({ length: totalPages }, (_, index) => (
                            <button
                                key={index}
                                onClick={() => handlePageChange(index)}
                                className={currentPage === index ? 'active' : ''}
                            >
                                {index + 1}
                            </button>
                        ))}
                        <button onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage + 1 >= totalPages}>
                            다음
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}
