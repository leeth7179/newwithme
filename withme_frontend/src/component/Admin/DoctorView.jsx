import { useParams } from "react-router-dom";
import React, { useState, useEffect } from "react";
import '../../css/DoctorList.css';
// import '../css/DoctorView.css';
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from '../../constant';

export default function DoctorView({doctor, onClose, docList }) {

/* 승인 상태 변경 버튼 이벤트
  - 승인 버튼을 누르면 해당 전문가의 userId와 승인 상태를 보내서 approveDoctorApplication 함수 실행
  - 여기있는 함수는 테스트용
 */
    const handleApprove = async (email, status) => {
        try {
            const response = await fetchWithAuth(`${API_URL}admin/doctor/approve/${email}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ status })
            });

             if (response.ok) {
                alert('상태 변경 성공');
                onClose();
                docList(); // 리스트 새로고침
            } else {
                console.error('상태 변경 실패', response.status);
            }
        } catch (error) {
            console.error('상태 변경 실패', error);
        }
    };

        return (
           <div className="modal" style={{ zIndex: "1000" }}>
                      <div className="modal-content">
                          <h2>전문가 상세 정보</h2>
                          <p><strong>이름:</strong> {doctor.member.name}</p>
                          <p><strong>전문분야:</strong> {doctor.subject}</p>
                          <p><strong>병원:</strong> {doctor.hospital}</p>
                          <p><strong>이메일:</strong> {doctor.member.email}</p>

                        {/* <button onClick={() => handleApprove(doctor.member.email, 'approved')}>승인</button>
                        <button onClick={() => handleApprove(doctor.member.email, 'rejected')}>거절</button>
                        <button onClick={() => handleApprove(doctor.member.email, 'on_hold')}>보류</button>
                        <button onClick={() => handleApprove(doctor.member.email, 'pending')}>대기</button> */}

                          <button onClick={onClose}>닫기</button>
                      </div>
                  </div>
        );
    };