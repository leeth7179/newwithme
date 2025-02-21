import React, { useState, useEffect } from 'react';
import { API_URL, SERVER_URL2 } from '../../constant';
import { useNavigate } from 'react-router-dom';
import '../../assets/css/Admin/ItemList_Admin.css';
import { fetchWithAuth } from '../../common/fetchWithAuth';

export default function ItemList() {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('');
    const [debouncedQuery, setDebouncedQuery] = useState('');
    const navigate = useNavigate();

    const itemsPerPage = 10;

    const fetchItems = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetchWithAuth(`${API_URL}admin/item/list`);
            const data = await response.json();
            setItems(data);
        } catch (err) {
            setError('상품 데이터를 가져오는 데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchItems();
    }, []);

    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedQuery(searchQuery);
        }, 500);
        return () => clearTimeout(timer);
    }, [searchQuery]);

    const filteredData = items.filter((item) =>
        item.itemNm.toLowerCase().includes(debouncedQuery.toLowerCase()) &&
        (statusFilter === '' || item.itemSellStatus === statusFilter)
    );

    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const currentData = filteredData.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

    const handlePageChange = (page) => {
        if (page >= 1 && page <= totalPages) {
            setCurrentPage(page);
        }
    };

    const handleViewDetail = (itemId) => {
        navigate(`/item/view/${itemId}`);
    };

    return (
        <div className="item-list-container">
            <h1 className="title">상품 목록</h1>

            <div className="search-bar">
                <input
                    type="text"
                    placeholder="상품명 검색"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />
                <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                    <option value="">전체</option>
                    <option value="SELL">판매중</option>
                    <option value="SOLD_OUT">품절</option>
                </select>
            </div>

            {loading ? (
                <p className="loading">데이터를 불러오는 중...</p>
            ) : error ? (
                <p className="error">{error}</p>
            ) : (
                <>
                    <table className="item-table">
                        <thead>
                            <tr>
                                <th>상품명</th>
                                <th>가격</th>
                                <th>재고</th>
                                <th>상태</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentData.length > 0 ? (
                                currentData.map((item) => (
                                    <tr key={item.id}>
                                        <td>{item.itemNm}</td>
                                        <td>{item.price.toLocaleString()}원</td>
                                        <td>{item.stockNumber}</td>
                                        <td>{item.itemSellStatus === 'SELL' ? '판매중' : '품절'}</td>
                                        <td>
                                            <button onClick={() => handleViewDetail(item.id)}>상세보기</button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="6">검색 결과가 없습니다.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>

                    <div className="pagination">
                        <button onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1}>
                            이전
                        </button>
                        {Array.from({ length: totalPages }, (_, index) => (
                            <button
                                key={index + 1}
                                onClick={() => handlePageChange(index + 1)}
                                className={currentPage === index + 1 ? 'active' : ''}
                            >
                                {index + 1}
                            </button>
                        ))}
                        <button onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages}>
                            다음
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}
