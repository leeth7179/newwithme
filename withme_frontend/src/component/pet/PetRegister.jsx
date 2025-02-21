import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  Button,
  TextField,
  Typography,
  Box,
  FormControl,
  Checkbox,
  FormControlLabel,
  Snackbar,
  Alert
} from "@mui/material";
import { PhotoCamera } from "@mui/icons-material";
import { API_URL } from "../../constant";
import { getImageUrl } from "../../utils/imageUtils";
import AllergySelection from "./AllergySelection";
import styles from "../../assets/css/member/PetRegister.module.css";

const PetRegister = ({ petData = null, onSubmitSuccess = () => {} }) => {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);

  const [formData, setFormData] = useState({
    name: "",
    breed: "",
    age: "",
    weight: "",
    gender: "M",
    healthConditions: "",
    neutered: false,
    imageFile: null
  });

  const [imagePreview, setImagePreview] = useState(null);
  const [error, setError] = useState("");
  const [selectedAllergies, setSelectedAllergies] = useState([]);

  // 초기 데이터 로드
  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (petData) {
      setFormData({
        name: petData.name || "",
        breed: petData.breed || "",
        age: petData.age ? String(petData.age) : "",
        weight: petData.weight ? String(petData.weight) : "",
        gender: petData.gender || "M",
        healthConditions: petData.healthConditions || "",
        neutered: petData.neutered || false,
        imageFile: null
      });

      if (petData.imageUrl) {
        setImagePreview(getImageUrl(petData.imageUrl));
      }

      // 알러지 데이터 설정 수정
      if (petData.allergies && Array.isArray(petData.allergies)) {
        const allergyIds = petData.allergies.map(
          (allergy) => allergy.substanceId
        );
        setSelectedAllergies(allergyIds);
      }
    }
  }, [petData, user, navigate]);

  // 입력값 변경 핸들러
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // 체크박스 변경 핸들러
  const handleCheckboxChange = (e) => {
    const { name, checked } = e.target;
    setFormData((prev) => ({ ...prev, [name]: checked }));
  };

  // 이미지 변경 핸들러
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setFormData((prev) => ({ ...prev, imageFile: file }));

      const reader = new FileReader();
      reader.onloadend = () => setImagePreview(reader.result);
      reader.readAsDataURL(file);
    }
  };

  // 알러지 선택 핸들러
  const handleAllergyChange = (substanceId) => {
    setSelectedAllergies((prev) => {
      if (prev.includes(substanceId)) {
        return prev.filter((id) => id !== substanceId);
      } else {
        return [...prev, substanceId];
      }
    });
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!user) {
      alert("로그인이 필요합니다.");
      return;
    }

    // 필수 입력 필드 검증
    if (
      !formData.name ||
      !formData.breed ||
      !formData.age ||
      !formData.weight
    ) {
      alert("필수 정보를 모두 입력해주세요.");
      return;
    }

    try {
      const url = petData
        ? `${API_URL}pets/${petData.petId}`
        : `${API_URL}pets/register`;
      const method = petData ? "PUT" : "POST";

      const dataToSend = new FormData();
      dataToSend.append("name", formData.name);
      dataToSend.append("breed", formData.breed);
      dataToSend.append("age", String(formData.age));
      dataToSend.append("weight", String(formData.weight));
      dataToSend.append("gender", formData.gender);
      dataToSend.append("userId", user.id);

      // 알러지 ID들을 쉼표로 구분된 문자열로 추가
      if (selectedAllergies && selectedAllergies.length > 0) {
        dataToSend.append("allergyIds", selectedAllergies.join(","));
      }

      if (formData.neutered !== undefined) {
        dataToSend.append("neutered", formData.neutered);
      }

      if (formData.healthConditions) {
        dataToSend.append("healthConditions", formData.healthConditions);
      }

      if (formData.imageFile) {
        dataToSend.append("image", formData.imageFile);
      }

      const response = await fetch(url, {
        method,
        body: dataToSend,
        credentials: "include"
      });

      if (response.ok) {
        const result = await response.json();
        onSubmitSuccess(result);
        alert(
          petData
            ? "반려동물 정보가 수정되었습니다."
            : "반려동물이 등록되었습니다."
        );
        navigate(`/mypage/${user.id}`);
      } else {
        const errorText = await response.text();
        console.error("펫 등록/수정 오류:", errorText);

        switch (response.status) {
          case 400:
            setError("잘못된 요청입니다. 입력 정보를 확인해주세요.");
            break;
          case 401:
            alert("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
            navigate("/login");
            break;
          case 403:
            setError("해당 작업을 수행할 권한이 없습니다.");
            break;
          case 500:
            setError("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            break;
          default:
            setError("펫 등록/수정 중 오류가 발생했습니다.");
        }
      }
    } catch (error) {
      console.error("펫 등록/수정 중 오류:", error);
      setError("서버와 통신 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className={styles.form_container}>
      <div className={styles.form_warp}>
        <p className={styles.form_titme}>
          {petData ? "펫 정보 수정" : "펫 등록"}
        </p>

        <form onSubmit={handleSubmit}>
          <div className={styles.grid_box}>
            <div className={styles.left_section}>
              {/* 이미지 업로드 */}
              <div className={styles.image_upload_container}>
                {imagePreview && (
                  <img
                    src={imagePreview}
                    alt="미리보기"
                    className={styles.image_preview}
                  />
                )}
                <Button
                  variant="outlined"
                  component="label"
                  startIcon={<PhotoCamera />}>
                  사진 {imagePreview ? "변경" : "추가"}
                  <input
                    type="file"
                    hidden
                    accept="image/*"
                    onChange={handleImageChange}
                  />
                </Button>
              </div>

              {/* 이름 */}
              <TextField
                label="이름"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                required
                fullWidth
                className={styles.input_field}
              />

              {/* 품종 */}
              <TextField
                label="품종"
                name="breed"
                value={formData.breed}
                onChange={handleInputChange}
                fullWidth
                className={styles.input_field}
              />

              {/* 나이 & 체중 */}
              <Box sx={{ display: "flex", gap: 2 }}>
                <TextField
                  label="나이"
                  name="age"
                  value={formData.age}
                  onChange={handleInputChange}
                  required
                  fullWidth
                  className={styles.input_field}
                />
                <TextField
                  label="체중 (kg)"
                  name="weight"
                  value={formData.weight}
                  onChange={handleInputChange}
                  required
                  fullWidth
                  className={styles.input_field}
                />
              </Box>

              {/* 성별 */}
              <div className={styles.gender_selection}>
                <Button
                  variant={formData.gender === "M" ? "contained" : "outlined"}
                  onClick={() =>
                    setFormData((prev) => ({ ...prev, gender: "M" }))
                  }
                  className={`${styles.gender_button} ${
                    formData.gender === "M" ? styles.active : ""
                  }`}>
                  수컷
                </Button>
                <Button
                  variant={formData.gender === "F" ? "contained" : "outlined"}
                  onClick={() =>
                    setFormData((prev) => ({ ...prev, gender: "F" }))
                  }
                  className={`${styles.gender_button} ${
                    formData.gender === "F" ? styles.active : ""
                  }`}>
                  암컷
                </Button>
              </div>

              {/* 중성화 여부 */}
              <FormControlLabel
                control={
                  <Checkbox
                    checked={formData.neutered}
                    onChange={handleCheckboxChange}
                    name="neutered"
                  />
                }
                label="중성화 여부"
                className={styles.neutered_checkbox}
              />

              {/* 건강 상태 */}
              <TextField
                label="건강 상태"
                name="healthConditions"
                value={formData.healthConditions}
                onChange={handleInputChange}
                multiline
                rows={3}
                fullWidth
                className={styles.health_conditions}
              />
            </div>

            <div className={styles.allergy_section}>
              <h3>우리 아이 알러지 체크</h3>
              <div className={styles.allergy_content}>
                <AllergySelection
                  selectedAllergies={selectedAllergies}
                  onAllergyChange={handleAllergyChange}
                />
              </div>
            </div>
          </div>
          <div className={styles.button_group}>
            <Button
              variant="outlined"
              onClick={() => navigate(`/mypage/${user.id}`)}
              fullWidth>
              취소
            </Button>
            <Button type="submit" variant="contained" fullWidth>
              {petData ? "수정 완료" : "등록"}
            </Button>
          </div>
        </form>

        {/* 오류 메시지 */}
        <Snackbar
          open={!!error}
          autoHideDuration={6000}
          onClose={() => setError("")}>
          <Alert severity="error">{error}</Alert>
        </Snackbar>
      </div>
    </div>
  );
};

export default PetRegister;