import React, { useState } from "react";
import { Box, Typography, Chip, Stack } from "@mui/material";

const AllergySelection = () => {
  const [selectedAllergies, setSelectedAllergies] = useState([]);

  const allergies = {
    "주 단백질원": [
      { id: 1, name: "소고기" },
      { id: 2, name: "닭고기" },
      { id: 3, name: "양고기" },
      { id: 4, name: "오리고기" },
      { id: 5, name: "칠면조고기" },
      { id: 6, name: "캥거루고기" },
      { id: 7, name: "흑염소고기" },
      { id: 8, name: "사슴고기" },
      { id: 9, name: "연어" },
      { id: 10, name: "대구" },
      { id: 11, name: "참치" },
      { id: 12, name: "삼치" },
      { id: 13, name: "멸치" },
      { id: 14, name: "새우" },
      { id: 15, name: "게" },
      { id: 16, name: "가재" }
    ],
    "보조 단백질": [
      { id: 17, name: "우유" },
      { id: 18, name: "치즈" },
      { id: 19, name: "계란" },
      { id: 20, name: "두유" },
      { id: 21, name: "두부" },
      { id: 22, name: "밀가루" },
      { id: 23, name: "고구마" },
      { id: 24, name: "감자" },
      { id: 25, name: "단호박" },
      { id: 26, name: "양배추" },
      { id: 27, name: "브로콜리" },
      { id: 28, name: "사과" },
      { id: 29, name: "바나나" },
      { id: 30, name: "크랜베리" }
    ],
    "곡물 및 견과류": [
      { id: 31, name: "귀리" },
      { id: 32, name: "옥수수" },
      { id: 33, name: "밀" },
      { id: 34, name: "땅콩" },
      { id: 35, name: "렌틸콩" }
    ],
    기타: [
      { id: 36, name: "꽃가루" },
      { id: 37, name: "세제" },
      { id: 38, name: "방향제" },
      { id: 39, name: "라텍스" },
      { id: 40, name: "금속" },
      { id: 41, name: "플라스틱" }
    ]
  };

  const handleAllergyToggle = (id) => {
    setSelectedAllergies((prev) =>
      prev.includes(id)
        ? prev.filter((allergyId) => allergyId !== id)
        : [...prev, id]
    );
  };

  const renderAllergyGroup = (title, items, index) => (
    <Box key={`allergy-group-${index}`} sx={{ mb: 3 }}>
      <Typography variant="subtitle1" sx={{ mb: 2, fontWeight: "bold" }}>
        {title}
      </Typography>
      <Stack direction="row" flexWrap="wrap" gap={1}>
        {items.map((allergy) => (
          <Chip
            key={allergy.id}
            label={allergy.name}
            variant={
              selectedAllergies.includes(allergy.id) ? "filled" : "outlined"
            }
            color={
              selectedAllergies.includes(allergy.id) ? "primary" : "default"
            }
            onClick={() => handleAllergyToggle(allergy.id)}
            sx={{
              mb: 1,
              "& .MuiChip-label": {
                fontWeight: selectedAllergies.includes(allergy.id)
                  ? "bold"
                  : "normal"
              }
            }}
          />
        ))}
      </Stack>
    </Box>
  );

  return (
    <Box>
      {/* <Typography
        variant="h6"
        sx={{
          mb: 3,
          fontWeight: "bold",
          color: "#333",
          textAlign: "center"
        }}>
        우리 아이 알러지 체크
      </Typography> */}
      {Object.entries(allergies).map(([title, items], index) =>
        renderAllergyGroup(title, items, index)
      )}
    </Box>
  );
};

export default AllergySelection;