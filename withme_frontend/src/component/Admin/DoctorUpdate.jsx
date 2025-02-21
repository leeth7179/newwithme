import React, { useState, useEffect } from 'react';
import { fetchWithAuth } from '../../common/fetchWithAuth';
import DoctorViewAdmin from './DoctorViewAdmin';
import '../../css/DoctorUpdate.css';

export default function DoctorUpdate() {
    const [pendingDoctors, setPendingDoctors] = useState([]); // 대기중 전문가 리스트 상태
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedDoctor, setSelectedDoctor] = useState(null);

    // DB에서 리스트 가져오기
    const fetchPendingDoctors = async () => {
        try {
            const response = await fetchWithAuth('http://localhost:8080/api/admin/doctor/pending');
            const data = await response.json();
            setPendingDoctors(data); // 서버에서 받은 데이터로 상태 업데이트
        } catch (err) {
            console.error('신청 리스트를 가져오는 데 실패', err);
        }
    };

    useEffect(() => {
        fetchPendingDoctors(); // 전문가 리스트 가져오기
    }, []);

    // 팝업 열기 함수
    const openModal = (doctor) => {
        setSelectedDoctor(doctor);
        setIsModalOpen(true);
    };

    // 팝업 닫기 함수
    const closeModal = () => {
        setIsModalOpen(false);
    };

    // 상태값 한글 변환
    const getStatusText = (status) => {
        switch (status) {
            case 'APPROVED':
                return '승인';
            case 'PENDING':
                return '대기';
            case 'REJECTED':
                return '거절';
            case 'ON_HOLD':
                return '보류';
            default:
                return status;
        }
    };

    return (
        <div className="doctor-update-container">
            <h1 className="title">승인 대기 목록</h1>
            <table className="doctor-table">
                <thead>
                    <tr>
                        <th>전문가번호</th>
                        <th>이름</th>
                        <th>담당과목</th>
                        <th>병원정보</th>
                        <th>상태</th>
                        <th>상세보기</th>
                    </tr>
                </thead>
                <tbody>
                    {pendingDoctors.length > 0 ? (
                        pendingDoctors.map((doctor) => (
                            <tr key={doctor.doctorId}>
                                <td>{doctor.doctorId}</td>
                                <td>{doctor.member.name}</td>
                                <td>{doctor.subject}</td>
                                <td>{doctor.hospital}</td>
                                <td>{getStatusText(doctor.status)}</td>
                                <td>
                                    <button className="detail-button" onClick={() => openModal(doctor)}>상세보기</button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="6" className="no-data">신청자가 없습니다.</td>
                        </tr>
                    )}
                </tbody>
            </table>

            {/* 팝업 모달 */}
            {isModalOpen && (
                <DoctorViewAdmin doctor={selectedDoctor} onClose={closeModal} docList={fetchPendingDoctors} />
            )}
        </div>
    );
}
