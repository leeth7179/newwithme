import React, { useState, useEffect } from "react";
import { API_URL } from "../constant";
import { useLocation } from "react-router-dom";

export default function SearchResults() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const location = useLocation();

  // URL에서 검색어와 petId 추출
  const query = new URLSearchParams(location.search);
  const searchQuery = query.get("query");
  const petId = query.get("petId") ? Number(query.get("petId")) : null;

  useEffect(() => {
    // 검색어가 없으면 검색하지 않음
    if (!searchQuery) return;

    const fetchSearchResults = async () => {
      setLoading(true);
      setError(null);

      try {
        // 기본 검색 API 엔드포인트 수정
        const url = `${API_URL}search?keyword=${encodeURIComponent(
          searchQuery
        )}`;

        const response = await fetch(url, {
          method: "GET",
          headers: {
            "Content-Type": "application/json"
          }
        });

        // 응답 확인
        if (!response.ok) {
          throw new Error("검색 중 오류가 발생했습니다.");
        }

        const data = await response.json();
        setItems(data);
      } catch (error) {
        setError("검색에 실패했습니다.");
        console.error("검색 오류:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchSearchResults();
  }, [searchQuery]);

  return (
    <div className="search-results-container">
      <h2>'{searchQuery}' 검색 결과</h2>

      {loading ? (
        <p>검색 중...</p>
      ) : error ? (
        <p className="error">{error}</p>
      ) : (
        <div className="item-grid">
          {items.length > 0 ? (
            items.map((item) => (
              <div className="item-card" key={item.id}>
                <div className="item-info">
                  <h3>{item.itemNm}</h3>
                  <p>{item.itemDetail}</p>
                  <p className="price">{item.price.toLocaleString()}원</p>
                  <p className="stock">재고: {item.stockNumber}</p>
                  <p className="status">상태: {item.itemSellStatus}</p>
                </div>
              </div>
            ))
          ) : (
            <p>검색 결과가 없습니다.</p>
          )}
        </div>
      )}
    </div>
  );
}
