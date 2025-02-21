import React, { useState } from "react";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";

export default function DoctorViewAdmin({ doctor, onClose, docList }) {
  const [status, setStatus] = useState(doctor.status); // 현재 상태
  const [reason, setReason] = useState(""); // 거절/보류 사유 입력

  // 승인 상태 변경 요청
  const handleApprove = async () => {
    if ((status === "REJECTED" || status === "ON_HOLD") && !reason.trim()) {
      alert("사유를 입력해주세요.");
      return;
    }

    const confirmMessage = `전문가 상태를 '${getStatusText(status)}'로 변경하시겠습니까?`;
    if (!window.confirm(confirmMessage)) return;

    try {
      const response = await fetchWithAuth(`${API_URL}admin/doctor/approve/${doctor.member.email}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status, reason }),
      });

      if (response.ok) {
        alert("상태 변경 성공");
        onClose();
        docList(); // 리스트 새로고침
      } else {
        console.error("상태 변경 실패", response.status);
      }
    } catch (error) {
      console.error("상태 변경 실패", error);
    }
  };

  // 상태값 한글 변환
  const getStatusText = (status) => {
    switch (status) {
      case "APPROVED":
        return "승인";
      case "PENDING":
        return "대기";
      case "REJECTED":
        return "거절";
      case "ON_HOLD":
        return "보류";
      default:
        return status;
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h2>전문가 상세 정보</h2>
        <p><strong>이름:</strong> {doctor.member.name}</p>
        <p><strong>이메일:</strong> {doctor.member.email}</p>
        <p><strong>연락처:</strong> {doctor.member.phone}</p>
        <p><strong>전문분야:</strong> {doctor.subject}</p>
        <p><strong>병원:</strong> {doctor.hospital}</p>
        <p><strong>가입일:</strong> {doctor.member.createdAt}</p>
        <p><strong>현재 상태:</strong> {getStatusText(doctor.status)}</p>

        {/* 상태 변경 드롭다운 */}
        <label>상태 변경:</label>
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="APPROVED">승인</option>
          <option value="PENDING">대기</option>
          <option value="REJECTED">거절</option>
          <option value="ON_HOLD">보류</option>
        </select>

        {/* 거절 또는 보류 선택 시 사유 입력 */}
        {(status === "REJECTED" || status === "ON_HOLD") && (
          <div className="reason-input">
            <label>사유 입력:</label>
            <textarea
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="사유를 입력해주세요"
              required
            />
          </div>
        )}

        {/* 상태 변경 버튼 */}
        <button className="modal-button modal-button-approve" onClick={handleApprove}>
          상태 변경
        </button>

        {/* 닫기 버튼 */}
        <button className="modal-button" onClick={onClose}>닫기</button>
      </div>
    </div>
  );
}
