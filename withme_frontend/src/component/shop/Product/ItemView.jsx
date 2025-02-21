import React, { useState, useEffect } from "react";
import "../../../assets/css/shop/ItemView.css";
import { API_URL, SERVER_URL2 } from "../../../constant";
import { fetchWithAuth } from "../../../common/fetchWithAuth";
import { useParams, useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faShoppingBasket, faCaretDown } from "@fortawesome/free-solid-svg-icons";

export default function ItemView({ user }) {
  const { itemId } = useParams();
  const [item, setItem] = useState(null); // 상품 상세 정보를 저장할 상태
  const [substances, setSubstances] = useState([]); // 알러지 성분 목록 추가
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [error, setError] = useState(null); // 에러 상태
  const navigate = useNavigate();

  console.log("받아온 user : ", user);

  // 알러지 성분 목록 불러오기
  useEffect(() => {
    const fetchSubstances = async () => {
      try {
        const response = await fetchWithAuth(`${API_URL}substances/list`);
        if (!response.ok) {
          throw new Error("알러지 성분 목록을 불러오는 데 실패했습니다.");
        }
        const data = await response.json();
        setSubstances(data);
      } catch (error) {
        console.error("알러지 성분 목록 오류:", error);
      }
    };

    fetchSubstances();
  }, []);

  // 알러지 성분 이름 찾기 함수
  const getSubstanceNames = (substanceIds) => {
    if (!substanceIds || substanceIds.length === 0) return "없음";
    return substanceIds
      .map((id) => substances.find((s) => s.substanceId === id)?.name)
      .filter((name) => name)
      .join(", ");
  };

  // 상품 상세 정보를 가져오는 함수
  useEffect(() => {
    if (!itemId) return;

    const fetchItemDetail = async () => {
      try {
        const response = await fetch(`${API_URL}item/view/${itemId}`); // 서버에서 상품 상세 정보 요청
        if (!response.ok) {
          throw new Error("상품 정보를 불러오는 데 실패했습니다.");
        }
        const data = await response.json();
        console.log("상품 상세정보 호출 :", data);
        setItem(data); // 응답 받은 데이터로 상태 업데이트
        setLoading(false);
      } catch (error) {
        setError(error.message); // 에러 상태 업데이트
        setLoading(false);
      }
    };

    fetchItemDetail(); // 상품 상세 정보를 요청
  }, [itemId]); // itemId가 변경될 때마다 실행

  if (loading) {
    return <div>로딩 중...</div>; // 로딩 중일 때 표시할 UI
  }

  if (error) {
    return <div>{error}</div>; // 에러 발생 시 표시할 UI
  }

  if (!item) {
    return <div>상품을 찾을 수 없습니다.</div>; // item이 없을 경우 표시할 UI
  }

  // 장바구니 추가 함수
  const handleAddToCart = async () => {
    if (!user) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      const cartItem = {
        itemId: item.id,
        count: 1 // 기본 수량 1개
      };

      const response = await fetchWithAuth(`${API_URL}cart/add`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cartItem)
      });

      if (response.ok) {
        alert("장바구니에 추가되었습니다.");
      } else {
        const errorMsg = await response.text();
        alert(`장바구니 추가 실패: ${errorMsg}`);
      }
    } catch (error) {
      console.error("장바구니 추가 오류:", error);
      alert("장바구니 추가 중 오류가 발생했습니다.");
    }
  };

  // 상세보기 페이지로 이동
  const handleEdit = (itemId) => {
    navigate(`/item/edit/${itemId}`);
  };

  const handleDelete = async () => {
    // 삭제 요청 처리
    try {
      const response = await fetchWithAuth(`${API_URL}item/delete/${itemId}`, {
        method: "PATCH"
      });
      if (response.ok) {
        alert("상품이 삭제되었습니다.");
        // 삭제 후 리스트로 이동 추가
        navigate("/item/list");
      } else {
        alert("상품 삭제에 실패했습니다.");
      }
    } catch (error) {
      console.error("삭제 실패:", error);
      alert("상품 삭제 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="item-view-container">
      <div className="item-view-content">
        {/* 상품 이미지 */}
        {item.itemImgDtoList && item.itemImgDtoList.length > 0 && (
          <div className="item-image-container">
            <img
              src={`${SERVER_URL2}${item.itemImgDtoList[0].imgUrl}`}
              alt={item.itemNm}
              className="item-image"
            />
          </div>
        )}

        <div className="item-details">
          <h2>{item.itemNm}</h2>
          <p>
            <strong>가격:</strong> {item.price.toLocaleString()}원
          </p>
          <p>
            <strong>재고:</strong> {item.stockNumber}
          </p>
          <p>
            <strong>설명:</strong> {item.itemDetail}
          </p>
          {/* 알러지 성분 정보 추가 */}
          <p>
            <strong>알러지 성분:</strong> {getSubstanceNames(item.substanceIds)}
          </p>

          <button className="add-to-cart-btn" onClick={handleAddToCart} style={{ width: "200px" }}>
            <FontAwesomeIcon icon={faShoppingBasket} style={{ marginRight: "10px", fontSize: "1.5em" }} />장바구니 담기
          </button>

          {/* 관리자인 경우에만 수정 및 삭제 버튼을 표시 */}
          {user && user.roles && user.roles.includes("ROLE_ADMIN") && (
            <div className="admin-actions">
              <button onClick={() => handleEdit(item.id)}>상품 수정</button>
              <button onClick={handleDelete}>상품 삭제</button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
