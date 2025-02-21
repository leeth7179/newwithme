import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import {
  Typography,
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from "@mui/material";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import { getImageUrl } from "../../utils/imageUtils";
import PetRegister from "./PetRegister";
// PetRegister.jsx
import PetRegisterButtons from "./PetRegisterButtons";
import "../../assets/css/member/mypage.css";

// 알러지 정보를 표시하는 컴포넌트
const AllergiesSection = ({ allergies }) => {
  if (!allergies || allergies.length === 0) return null;

  return (
    <Box sx={{ mt: 3, p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
      <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: "bold" }}>
        우리 아이 알러지
      </Typography>
      <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
        {allergies.map((allergy) => (
          <Box
            key={allergy.substanceId}
            sx={{
              backgroundColor: "primary.main",
              color: "white",
              px: 2,
              py: 0.5,
              borderRadius: 4,
              fontSize: "0.875rem"
            }}>
            {allergy.name}
          </Box>
        ))}
      </Box>
    </Box>
  );
};

//상세 정보 아이템 컴포넌트
const DetailItem = ({ label, value }) => (
  <Box sx={{ p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
      {label}
    </Typography>
    <Typography variant="body1" sx={{ fontWeight: "medium" }}>
      {value}
    </Typography>
  </Box>
);

const PetDetailPage = () => {
  const { petId } = useParams();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);

  const [petDetails, setPetDetails] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [consultationStatus, setConsultationStatus] = useState({
    simpleSurveyCompleted: false,
    paymentCompleted: false,
    expertSurveyCompleted: false
  });
  const [isPaymentDialogOpen, setIsPaymentDialogOpen] = useState(false);

  useEffect(() => {
    const fetchPetDetails = async () => {
      if (!petId) return;

      setIsLoading(true);
      try {
        const response = await fetchWithAuth(`${API_URL}pets/${petId}`);

        if (response.ok) {
          const result = await response.json();
          setPetDetails(result);
          setConsultationStatus({
            simpleSurveyCompleted: result.simpleSurveyCompleted || false,
            paymentCompleted: result.paymentCompleted || false,
            expertSurveyCompleted: result.expertSurveyCompleted || false
          });
        } else if (response.status === 404) {
          throw new Error("해당 반려동물을 찾을 수 없습니다.");
        } else {
          throw new Error("서버 오류가 발생했습니다.");
        }
      } catch (error) {
        console.error("펫 상세 정보 로드 오류:", error.message);
        alert(error.message || "펫 정보를 불러오는 중 문제가 발생했습니다.");
        navigate(`/mypage/${user.id}`);
      } finally {
        setIsLoading(false);
      }
    };

    fetchPetDetails();
  }, [petId, navigate, user.id]);

  // 문진, 결제, 설문 관련 핸들러들
  const handleSimpleSurveyComplete = async () => {
    // 기존 간단 문진 완료 로직
  };

  const handleOpenPaymentDialog = () => {
    // 결제 다이얼로그 오픈 로직
  };

  const handlePayment = async () => {
    // 결제 처리 로직
  };

  const handleExpertSurveyComplete = async () => {
    // 전문의 문진 완료 로직
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleUpdateSuccess = (updatedPet) => {
    setPetDetails(updatedPet);
    setIsEditing(false);
  };

  const handleDelete = async () => {
    if (!window.confirm("정말로 삭제하시겠습니까?")) {
      return;
    }

    try {
      const response = await fetchWithAuth(`${API_URL}pets/${petId}`, {
        method: "DELETE"
      });

      if (response.ok) {
        alert("반려동물 정보가 삭제되었습니다.");
        navigate(`/mypage/${user.id}`);
      } else {
        const error = await response.text();
        console.error("삭제 실패:", error);
        alert("삭제에 실패했습니다.");
      }
    } catch (error) {
      console.error("삭제 중 오류:", error);
      alert("삭제 중 오류가 발생했습니다.");
    }
  };

  if (isLoading || !petDetails) {
    return (
      <div className="page_container">
        <h2 className="page_title">반려동물 상세 정보</h2>
        <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
          <Typography variant="h5">로딩 중...</Typography>
        </Box>
      </div>
    );
  }

  const isOwner = user?.id === petDetails?.userId;

  return (
    <>
      {isEditing ? (
        <div className="form_container_1tzal_1">
          <PetRegister
            petData={petDetails}
            onSubmitSuccess={handleUpdateSuccess}
            isEditing={true}
          />
        </div>
      ) : (
        <div className="page_container">
          <h2 className="page_title">반려동물 상세 정보</h2>

          <div className="my_wrap">
            <div className="grid_box">
              <div className="left_section">
                <div className="section">
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "center",
                      mb: 4
                    }}>
                    <img
                      src={getImageUrl(petDetails.imageUrl)}
                      alt={petDetails.name}
                      onError={(e) => {
                        e.target.src = "/assets/images/default-pet-image.png";
                      }}
                      style={{
                        width: "200px",
                        height: "200px",
                        borderRadius: "50%",
                        objectFit: "cover"
                      }}
                    />
                  </Box>

                  <Box
                    sx={{
                      display: "grid",
                      gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" },
                      gap: 2,
                      mb: 3
                    }}>
                    <DetailItem label="이름" value={petDetails.name} />
                    <DetailItem label="나이" value={`${petDetails.age}세`} />
                    <DetailItem
                      label="품종"
                      value={petDetails.breed || "미등록"}
                    />
                    <DetailItem
                      label="성별"
                      value={petDetails.gender === "M" ? "수컷" : "암컷"}
                    />
                    <DetailItem label="체중" value={`${petDetails.weight}kg`} />
                    <DetailItem
                      label="중성화"
                      value={petDetails.neutered ? "완료" : "미완료"}
                    />
                  </Box>

                  {petDetails.healthConditions && (
                    <Box
                      sx={{
                        mt: 3,
                        p: 2,
                        bgcolor: "grey.50",
                        borderRadius: 1
                      }}>
                      <Typography
                        variant="subtitle1"
                        sx={{ mb: 1, fontWeight: "bold" }}>
                        건강 상태
                      </Typography>
                      <Typography variant="body1">
                        {petDetails.healthConditions}
                      </Typography>
                    </Box>
                  )}

                  {/* 문진 섹션 */}
                  <Box
                    sx={{
                      display: "flex",
                      flexDirection: "column",
                      gap: 2,
                      mt: 4
                    }}>
                    {/* 간단 문진 */}
                    <div className="flex_box">
                      <Typography variant="body2" color="text.secondary">
                        펫 상태를 간단하게 체크해보세요!
                      </Typography>
                      <Button
                        variant={
                          consultationStatus.simpleSurveyCompleted
                            ? "contained"
                            : "outlined"
                        }
                        onClick={handleSimpleSurveyComplete}
                        color="primary"
                        disabled={consultationStatus.simpleSurveyCompleted}>
                        {consultationStatus.simpleSurveyCompleted
                          ? "문진 완료"
                          : "문진 작성"}
                      </Button>
                    </div>

                    {/* 전문의 문진 */}
                    <div className="flex_box">
                      <Typography variant="body2" color="text.secondary">
                        자세한 문진 작성 후 전문의에게
                        <br />
                        상담을 받으세요!
                      </Typography>
                      <Button
                        variant="outlined"
                        onClick={handleOpenPaymentDialog}
                        color="primary"
                        disabled={!consultationStatus.simpleSurveyCompleted}>
                        결제하기
                      </Button>
                      <Button
                        variant={
                          consultationStatus.expertSurveyCompleted
                            ? "contained"
                            : "outlined"
                        }
                        onClick={handleExpertSurveyComplete}
                        color="primary"
                        disabled={
                          !consultationStatus.paymentCompleted ||
                          consultationStatus.expertSurveyCompleted
                        }>
                        {consultationStatus.expertSurveyCompleted
                          ? "전문의 문진 완료"
                          : "전문의 문진 작성"}
                      </Button>
                    </div>
                  </Box>
                </div>
              </div>
              <div className="info_section">
                <div>
                  <Box className="section_header">
                    <Typography variant="h5" component="h2">
                      펫 관리
                    </Typography>
                  </Box>
                  <AllergiesSection allergies={petDetails.allergies} />
                </div>

                <PetRegisterButtons
                  petData={petDetails}
                  user={user}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                />
              </div>
            </div>
          </div>

          <Dialog
            open={isPaymentDialogOpen}
            onClose={() => setIsPaymentDialogOpen(false)}
            aria-labelledby="payment-dialog-title">
            <DialogTitle id="payment-dialog-title">
              전문의 상담 결제
            </DialogTitle>
            <DialogContent>
              <Typography variant="body1">
                전문의 문진 상담을 위해 결제를 진행하시겠습니까?
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                결제 금액: 50,000원
              </Typography>
            </DialogContent>
            <DialogActions>
              <Button
                onClick={() => setIsPaymentDialogOpen(false)}
                color="primary">
                취소
              </Button>
              <Button
                onClick={handlePayment}
                color="primary"
                variant="contained">
                결제
              </Button>
            </DialogActions>
          </Dialog>
        </div>
      )}
    </>
  );
};

export default PetDetailPage;