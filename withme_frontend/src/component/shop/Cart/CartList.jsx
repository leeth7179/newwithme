import React, { useState, useEffect } from 'react';
import { API_URL, SERVER_URL2 } from '../../../constant';
import { fetchWithAuth } from '../../../common/fetchWithAuth';
import { useNavigate } from 'react-router-dom';
import '../../../assets/css/shop/CartList.css';

export default function CartPage() {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedItems, setSelectedItems] = useState([]); // 선택된 상품 목록
    const [deleteMessage, setDeleteMessage] = useState(''); // 삭제 메시지 상태
    const navigate = useNavigate();

    // 장바구니 목록 조회
    useEffect(() => {
        const fetchCartItems = async () => {
            try {
                const response = await fetchWithAuth(`${API_URL}cart/list`);
                if (!response.ok) {
                    throw new Error('장바구니 정보를 불러오지 못했습니다.');
                }
                const data = await response.json();
                console.log("받아온 장바구니 목록 : ", data);
                setCartItems(data);
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };
        fetchCartItems();
    }, []);

    // 상품 삭제
    const handleDelete = async (cartItemId) => {
        try {
            const response = await fetchWithAuth(`${API_URL}cart/cartItem/${cartItemId}`, {
                method: 'DELETE',
            });
            if (!response.ok) {
                throw new Error('삭제 실패');
            }
            setCartItems(prevItems => prevItems.filter(item => item.cartItemId !== cartItemId));
            console.log(`삭제된 상품 ID: ${cartItemId}`);

            // 삭제 메시지 설정
            setDeleteMessage('장바구니에서\n상품이 삭제됐어요.');
            setTimeout(() => setDeleteMessage(''), 3000);
        } catch (error) {
            alert(error.message);
        }
    };

    // 수량 조절 함수
    const handleQuantityChange = (cartItemId, newCount) => {
        if (newCount < 1) {
            alert('수량은 1개 이상이어야 합니다.');
            return;
        }
        setCartItems(prevItems =>
            prevItems.map(item =>
                item.cartItemId === cartItemId ? { ...item, count: newCount } : item
            )
        );
    };

    // 선택된 상품 삭제
    const handleDeleteSelectedItems = async () => {
        if (selectedItems.length === 0) {
            alert('삭제할 상품을 선택해 주세요.');
            return;
        }

        // 사용자 확인 창
        const confirmDelete = window.confirm(`선택하신 ${selectedItems.length}개 상품을 장바구니에서 삭제하시겠습니까?`);
        if (!confirmDelete) return; // 사용자가 취소하면 삭제하지 않음

        try {
            // 선택된 상품 삭제 요청
            await Promise.all(
                selectedItems.map(cartItemId =>
                    fetchWithAuth(`${API_URL}cart/cartItem/${cartItemId}`, { method: 'DELETE' })
                )
            );

            // 삭제된 상품들만 필터링해서 업데이트
            setCartItems(prevItems => prevItems.filter(item => !selectedItems.includes(item.cartItemId)));
            setSelectedItems([]); // 선택된 항목 비우기
            console.log("선택된 상품 삭제 완료");

            // 삭제 메시지 설정
            setDeleteMessage('장바구니에서\n상품이 삭제됐어요.');
            setTimeout(() => setDeleteMessage(''), 3000); // 3초 후 메시지 사라지게
        } catch (error) {
            alert(error.message);
        }
    };

    // 장바구니 선택된 항목만 주문하기
    const handleOrder = async () => {
        if (selectedItems.length === 0) {
            alert('주문할 상품을 선택해 주세요.');
            return;
        }
        const orderData = { cartOrderItems: selectedItems.map(itemId => {
            const item = cartItems.find(item => item.cartItemId === itemId);
            console.log("선택된 상품:", item);
            return { cartItemId: item.cartItemId, count: item.count };
        })};

        console.log("주문할 데이터: ", orderData); // 선택된 항목과 수량 확인

        try {
            const response = await fetchWithAuth(`${API_URL}cart/orders`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(orderData),
            });
            if (!response.ok) {
                throw new Error('주문 실패');
            }
            // 서버에서 응답받은 데이터(주문 ID)를 콘솔에 출력
            const orderId = await response.json();
            console.log("반환받은 주문ID : ", orderId);  // 서버에서 반환된 주문ID

            navigate(`/orders/${orderId}`,{
                state: {orderData}
            });

        } catch (error) {
            alert(error.message);
        }
    };

    // 체크박스 변경
    const handleSelectItem = (cartItemId) => {
        setSelectedItems(prevSelectedItems => {
            if (prevSelectedItems.includes(cartItemId)) {
                return prevSelectedItems.filter(id => id !== cartItemId); // 이미 선택된 아이템은 제외
            } else {
                return [...prevSelectedItems, cartItemId]; // 선택되지 않은 아이템은 추가
            }
        });
    };

    // 전체 선택/해제
    const handleSelectAll = () => {
        if (selectedItems.length === cartItems.length) {
            setSelectedItems([]); // 이미 모든 항목이 선택되었으면 모두 해제
        } else {
            setSelectedItems(cartItems.map(item => item.cartItemId)); // 모든 항목을 선택
        }
    };

    // 선택된 상품들의 주문 가격 합산 계산
    const calculateTotalPrice = () => {
        return cartItems.reduce((total, item) => {
            if (selectedItems.includes(item.cartItemId)) {
                return total + (item.price * item.count);
            }
            return total;
        }, 0);
    };

    if (loading) return <div>로딩 중...</div>;
    if (error) return <div>{error}</div>;
    if (cartItems.length === 0) {
        return (
            <div className="empty-container">
                <img src="/assets/images/cart-dog.jpg" alt="강아지" />
                <div className="top-text">
                    <p className="empty">E / M / P / T / Y</p>
                    <p className="no-item">NO ITEM IN SHOPPING CART</p>
                </div>
                <div className="bottom-text">
                    <p>장바구니가 비어있습니다.</p>
                    <p>선택하신 상품을 장바구니에 담아주세요.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="cart-container">
            <h2>장바구니</h2>
            <div>
                <label>
                    <input
                        type="checkbox"
                        checked={selectedItems.length === cartItems.length}
                        onChange={handleSelectAll}
                    />
                    전체 선택
                </label>
                <button style={{ padding: "10px" }} className="delete-selected-button" onClick={handleDeleteSelectedItems}><span style={{ color: "#A7A7A7", marginRight: "5px" }}>X</span> 선택 삭제</button>
            </div>

            {cartItems.map(item => (
                <div key={item.cartItemId} className="cart-item">
                    <input
                        type="checkbox"
                        checked={selectedItems.includes(item.cartItemId)}
                        onChange={() => handleSelectItem(item.cartItemId)}
                    />
                    <img src={item.imgUrl ? SERVER_URL2 + item.imgUrl : '/assets/images/noImg.jpg'}
                     className="cart-item-image" />

                    <div className="cart-item-info">
                        <h4>{item.itemNm}</h4>
                        <p>{item.price.toLocaleString()}원</p>
                        <div className="count-wrap">
                            <button className="countBtn minusBtn" onClick={() => handleQuantityChange(item.cartItemId, item.count - 1)}>-</button>
                            <span style={{ border: "none", fontSize: "1em" }}>{item.count}</span>
                            <button className="countBtn plusBtn" onClick={() => handleQuantityChange(item.cartItemId, item.count + 1)}>+</button>
                        </div>
                        <p style={{ color: "black "}}>상품금액 <span style={{ fontWeight: "bold", marginLeft: "5px" }}>{(item.price * item.count).toLocaleString()}</span>원</p>
                        <button className="deleteBtn" onClick={() => handleDelete(item.cartItemId)}>X</button>
                    </div>
                </div>
            ))}

            {/* 삭제 메시지 */}
            {deleteMessage && (
                <div className="delete-message">
                    <img src="/assets/images/icon/cart-color.png" alt="cart-color" />
                    <p>{deleteMessage}</p>
                </div>
            )}

            {/* 최종 주문 가격 */}
            <div className="cart-footer">
                <div className="cart-footer-content">
                    <h3>총 {selectedItems.length}건<span style={{ margin: "0 8px" }} />주문금액 <span className="total-price">{calculateTotalPrice().toLocaleString()}</span>원</h3>
                    <button
                        className="order-button"
                        onClick={handleOrder}
                        disabled={selectedItems.length === 0}
                        >
                        주문하기
                    </button>
                </div>
            </div>
        </div>
    );
}
