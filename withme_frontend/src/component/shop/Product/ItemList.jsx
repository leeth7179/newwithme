import React, { useState, useEffect } from 'react';
import { API_URL, SERVER_URL2 } from '../../../constant';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faShoppingBasket, faSearch } from '@fortawesome/free-solid-svg-icons';
import { useSelector } from "react-redux";
import { fetchWithAuth } from '../../../common/fetchWithAuth'; // import fetchWithAuth 추가
import '../../../assets/css/shop/ItemList.css';

export default function ItemList() {
    const { itemId } = useParams();
    const { user, isLoggedIn } = useSelector((state) => state.auth);
    const navigate = useNavigate();
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [searchQuery, setSearchQuery] = useState('');
    const [cart, setCart] = useState([]); // useState로 장바구니 관리
    const itemsPerPage = 8;

    useEffect(() => {
        const fetchItems = async () => {
            setLoading(true);
            try {
                const response = await fetch(`${API_URL}item/list`);
                const data = await response.json();
                setItems(data);
            } catch (err) {
                setError('상품 데이터를 가져오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };
        fetchItems();
    }, []);

    const handleSurveyNavigation = (e) => {
        e.preventDefault();
        if (!isLoggedIn || !user) {
            alert('로그인이 필요한 서비스입니다.');
            navigate("/login");
            return;
        }

        if (user.role === "PAID" || user.role === "VIP") {
            navigate("/survey/paid");
        } else {
            navigate("/survey/free");
        }
    };

    const filteredItems = items.filter((item) =>
        item.itemNm.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
    const currentItems = filteredItems.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

    const handlePageChange = (page) => {
        if (page >= 1 && page <= totalPages) setCurrentPage(page);
    };

    // 장바구니 추가 함수
    const handleAddToCart = async (item) => {
        if (!user) {
            alert('로그인이 필요합니다.');
            return;
        }

        try {
            const cartItem = {
                itemId: item.id,
                count: 1 // 기본 수량 1개
            };

            const response = await fetchWithAuth(`${API_URL}cart/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(cartItem)
            });

            if (response.ok) {
                alert('장바구니에 추가되었습니다.');
            } else {
                const errorMsg = await response.text();
                alert(`장바구니 추가 실패: ${errorMsg}`);
            }
        } catch (error) {
            console.error('장바구니 추가 오류:', error);
            alert('장바구니 추가 중 오류가 발생했습니다.');
        }
    };

    return (
        <>
            <div className="navbar">
                <div className="navbar-links">
                    <a href="/" className="navbar-link">홈</a>
                    <a href="/item/list" className="navbar-link">쇼핑몰</a>
                    <a href="/notices" className="navbar-link">공지사항</a>
                    <a href="/posts" className="navbar-link">커뮤니티</a>
                </div>
                <div className="search-bar">
                    <FontAwesomeIcon icon={faSearch} className="search-icon" />
                    <input
                        type="text"
                        placeholder="어떤 상품을 찾아볼까요?"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        style={{ backgroundColor: "#F3F3F3", paddingLeft: "15px", border: "none" }}
                    />
                </div>
            </div>

            {/* 배너 */}
            <div
                className="green-banner"
                onClick={handleSurveyNavigation}
                style={{ cursor: "pointer" }}
            >
                <img src="/assets/images/green-banner.png" alt="배너 이미지" className="bannerImage" />
            </div>

            <div className="item-list-page">
                <div className="item-container">
                    <p className="item-title">전체 상품</p>
                    <div className="item-grid">
                        {loading ? (
                            <p>상품을 불러오는 중...</p>
                        ) : error ? (
                            <p className="error">{error}</p>
                        ) : filteredItems.length === 0 ? (
                            <div className="no-results-container">
                                <img src="/assets/images/searchDog.png" alt="cannotFound" className="cannotFound" />
                                <p>'{searchQuery}'에 대한 검색한 결과를 찾을 수가 없어요.</p>
                                <p>다른 검색어로 검색을 해보시겠어요?</p>
                            </div>
                        ) : (
                            currentItems.map((item) => (
                                <div className="item-card" key={item.id}>
                                    {item.itemImgDtoList?.length > 0 ? (
                                        <div className="image-container">
                                            <img
                                                src={`${SERVER_URL2}${item.itemImgDtoList[0].imgUrl}`}
                                                alt={item.itemNm}
                                                className="item-image"
                                                style={{ boxShadow: "none" }}
                                            />
                                            <button
                                                className="view-details-btn"
                                                onClick={() => navigate(`/item/view/${item.id}`)}
                                            >
                                                상세보기
                                            </button>
                                        </div>
                                    ) : (
                                        <div className="image-container">
                                            <img
                                                src="/assets/images/noImg.jpg"
                                                alt={item.itemNm}
                                                className="item-image"
                                                style={{ boxShadow: "none" }}
                                            />
                                            <button
                                                className="view-details-btn"
                                                onClick={() => navigate(`/item/view/${item.id}`)}
                                            >
                                                상세보기
                                            </button>
                                        </div>
                                    )}

                                    <div className="item-detail-wrap">
                                        <h3 className="itemName">{item.itemNm}</h3>
                                        <div className="price-cart-container">
                                            <p className="price">{item.price.toLocaleString()}원</p>
                                           <button
                                                className="add-to-cart-btn"
                                                onClick={() => handleAddToCart(item)}
                                                disabled={item.itemSellStatus === 'SOLD_OUT'}
                                            >
                                                <img src="/assets/images/icon/cart.png" alt="cart" className="cartIcon" />
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>

                    {/* 페이지네이션 */}
                    {filteredItems.length > 0 && (
                        <div className="pagination">
                            <button
                                onClick={() => handlePageChange(currentPage - 1)}
                                disabled={currentPage === 1}
                                style={{ color: "black", backgroundColor: "white", width: "80px" }}
                            >
                                이전
                            </button>
                            {Array.from({ length: totalPages }, (_, index) => {
                                const isActive = currentPage === index + 1;
                                return (
                                    <button
                                        key={index}
                                        onClick={() => handlePageChange(index + 1)}
                                        className={isActive ? 'active' : ''}
                                        style={{
                                            color: "black",
                                            fontWeight: isActive ? "bold" : "normal",
                                            background: isActive ? "#ccc" : "transparent",
                                            border: "none",
                                            borderRadius: "50%",
                                            cursor: "pointer",
                                            margin: "0 5px",
                                            width: "40px",
                                            height: "40px"
                                        }}
                                    >
                                        {index + 1}
                                    </button>
                                );
                            })}
                            <button
                                onClick={() => handlePageChange(currentPage + 1)}
                                disabled={currentPage === totalPages}
                                style={{ color: "black", backgroundColor: "white", width: "80px" }}
                            >
                                다음
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}