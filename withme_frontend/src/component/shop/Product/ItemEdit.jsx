import {
  Button,
  TextField,
  Typography,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  FormHelperText,
  Checkbox,
  ListItemText
} from "@mui/material";
import React, { useState, useEffect } from "react";
import { fetchWithAuth } from "../../../utils/fetchWithAuth"; // 절대 변경 금지 ( utils )
import { API_URL, SERVER_URL2 } from "../../../constant";
import { useNavigate, useParams } from "react-router-dom"; // 상품 ID를 가져오기 위해 useParams 사용
import "../../../assets/css/shop/ItemAdd.css";

const ItemEdit = () => {
  const [item, setItem] = useState({
    itemNm: "",
    price: "",
    itemDetail: "",
    stockNumber: "",
    itemSellStatus: "SELL",
    itemImgIds: [], // 기존 이미지 ID 리스트를 상태로 추가
    substanceIds: [] // 알러지 성분 추가
  });
  const [images, setImages] = useState([]);
  const [imagePreviews, setImagePreviews] = useState([]);
  const [loading, setLoading] = useState(true); // 로딩 상태 추가
  const [substances, setSubstances] = useState([]); // 알러지 성분 목록 상태

  const navigate = useNavigate();
  const { itemId } = useParams(); // URL에서 itemId를 가져옵니다.

  console.log("itemId : ", itemId);

  // 알러지 성분 목록 불러오기
  useEffect(() => {
    const fetchSubstances = async () => {
      try {
        const response = await fetchWithAuth(`${API_URL}substances/list`);
        if (response.ok) {
          const data = await response.json();
          setSubstances(data);
        }
      } catch (error) {
        console.error("알러지 성분 목록 불러오기 실패:", error);
      }
    };

    fetchSubstances();
  }, []);

  // 알러지 성분 선택 핸들러
  const handleSubstanceChange = (e) => {
    const { value } = e.target;
    setItem((prevItem) => ({
      ...prevItem,
      substanceIds: value
    }));
  };

  // 기존 상품 정보 불러오기
  useEffect(() => {
    const fetchItem = async () => {
      try {
        const response = await fetchWithAuth(`${API_URL}item/view/${itemId}`);
        if (response.ok) {
          const data = await response.json();
          setItem((prev) => ({
            ...data,
            substanceIds: data.substanceIds || [] // 알러지 성분 ID 추가
          }));

          // 기존 이미지 미리보기 설정
          const previews = data.itemImgDtoList.map((img) => img.imgUrl);
          setImagePreviews(previews);
        } else {
          alert("상품을 불러오는 데 실패했습니다.");
        }
      } catch (error) {
        console.error("상품 정보 불러오기 실패:", error);
        alert("상품 정보 불러오기 실패");
      } finally {
        setLoading(false);
      }
    };

    fetchItem();
  }, [itemId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setItem((prevItem) => ({
      ...prevItem,
      [name]: value
    }));
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImages(files);

    const previewUrls = files.map((file) => URL.createObjectURL(file));
    console.log("이미지 변경 후 URL : ", previewUrls);
    setImagePreviews(previewUrls);
  };

  const handleRemoveImage = (index) => {
    const newImages = images.filter((_, idx) => idx !== index);
    const newPreviews = imagePreviews.filter((_, idx) => idx !== index);
    setImages(newImages);
    setImagePreviews(newPreviews);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();

    formData.append(
      "itemFormDto",
      new Blob([JSON.stringify(item)], { type: "application/json" })
    );

    // 기존 이미지 ID들을 전송하는 과정
    item.itemImgDtoList.forEach((img) => {
      formData.append("itemImgIds", img.id); // itemImgDtoList에서 id 값을 가져와서 서버로 전달
    });

    images.forEach((image) => {
      formData.append("itemImgFile", image);
    });

    // formData 내용 확인
    for (let [key, value] of formData.entries()) {
      console.log(key, value);
    }

    try {
      const response = await fetchWithAuth(`${API_URL}item/edit/${itemId}`, {
        method: "PUT",
        body: formData
      });

      if (response.ok) {
        alert("상품이 성공적으로 수정되었습니다.");
        navigate("/item/list");
      } else {
        const errorData = await response.json();
        alert(`상품 수정 실패: ${errorData.message}`);
      }
    } catch (error) {
      const errorText = await response.text(); // 텍스트 형식으로 응답 받기
      alert(`상품 수정 중 오류 발생: ${errorText}`);
    }
  };

  if (loading) {
    return <div>로딩 중...</div>; // 로딩 중에 표시할 메시지
  }

  return (
    <div className="container">
      <Typography variant="h4" gutterBottom>
        📦 상품 수정
      </Typography>
      <Typography variant="body1" color="textSecondary" paragraph>
        상품 정보를 수정합니다.
      </Typography>

      <form onSubmit={handleSubmit} className="form-container">
        <div className="form-group">
          <TextField
            label="상품명"
            variant="outlined"
            fullWidth
            required
            name="itemNm"
            value={item.itemNm}
            onChange={handleChange}
            placeholder="상품명"
          />
        </div>

        <div className="form-group">
          <TextField
            label="판매가"
            variant="outlined"
            fullWidth
            required
            name="price"
            value={item.price}
            onChange={handleChange}
            placeholder="0"
            type="number"
          />
        </div>

        <div className="form-group">
          <Typography variant="body1">상품 이미지 등록</Typography>
          <div className="image-upload">
            <label className="upload-box">
              {imagePreviews.length === 0 && <span>📷 + 등록</span>}

              <input
                type="file"
                multiple
                className="hidden"
                onChange={handleImageChange}
              />
              {imagePreviews.map((preview, index) => (
                <div
                  key={index}
                  className="image-preview-container"
                  style={{ position: "relative" }}>
                  <img
                    src={
                      preview.includes("http")
                        ? preview
                        : `${SERVER_URL2}${preview}`
                    }
                    alt={`preview-${index}`}
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover"
                    }}
                  />
                  <button
                    type="button"
                    onClick={() => handleRemoveImage(index)}
                    style={{
                      position: "absolute",
                      width: "5px",
                      height: "5px",
                      top: "-15px",
                      right: "5px",
                      background: "transparent",
                      border: "none",
                      color: "red",
                      fontSize: "20px",
                      cursor: "pointer",
                      zIndex: 10
                    }}>
                    ✖
                  </button>
                </div>
              ))}
            </label>
          </div>
          <Typography
            variant="body2"
            color="textSecondary"
            className="info-text">
            권장 이미지: 500px × 500px / 5M 이하 / gif, png, jpg(jpeg)
          </Typography>
        </div>

        <div className="form-group">
          <TextField
            label="상품 수량"
            variant="outlined"
            fullWidth
            name="stockNumber"
            value={item.stockNumber}
            onChange={handleChange}
            placeholder="상품 수량"
          />
        </div>

        <div className="form-group">
          <TextField
            label="상품 상세 설명"
            variant="outlined"
            fullWidth
            multiline
            rows={4}
            name="itemDetail"
            value={item.itemDetail}
            onChange={handleChange}
            placeholder="상품 설명"
          />
        </div>
        {/* 알러지 성분 선택 섹션 */}
        <div className="form-group">
          <FormControl fullWidth>
            <InputLabel>알러지 성분</InputLabel>
            <Select
              multiple
              value={item.substanceIds}
              onChange={handleSubstanceChange}
              renderValue={(selected) =>
                selected
                  .map(
                    (id) => substances.find((s) => s.substanceId === id)?.name
                  )
                  .join(", ")
              }>
              {substances.map((substance) => (
                <MenuItem
                  key={substance.substanceId}
                  value={substance.substanceId}>
                  <Checkbox
                    checked={item.substanceIds.includes(substance.substanceId)}
                  />
                  <ListItemText primary={substance.name} />
                </MenuItem>
              ))}
            </Select>
            <FormHelperText>알러지 성분을 선택하세요</FormHelperText>
          </FormControl>
        </div>
        <div className="form-group">
          <FormControl fullWidth required>
            <InputLabel id="sell-status-label">판매 상태</InputLabel>
            <Select
              labelId="sell-status-label"
              name="itemSellStatus"
              value={item.itemSellStatus}
              onChange={handleChange}
              label="판매 상태">
              <MenuItem value="SELL">판매중</MenuItem>
              <MenuItem value="SOLD_OUT">품절</MenuItem>
            </Select>
            <FormHelperText>판매 상태를 선택하세요</FormHelperText>
          </FormControl>
        </div>

        <div className="button-container">
          <Button variant="contained" color="primary" type="submit">
            상품 수정
          </Button>
        </div>
      </form>
    </div>
  );
};

export default ItemEdit;
