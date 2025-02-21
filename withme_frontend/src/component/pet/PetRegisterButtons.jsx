import React from "react";
import { Button, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";

const PetRegisterButtons = ({ petData, user, onEdit, onDelete }) => {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        gap: 2
      }}>
      <Button variant="contained" fullWidth onClick={onEdit} color="primary">
        정보 수정
      </Button>
      <Button variant="outlined" fullWidth onClick={onDelete} color="error">
        펫 삭제
      </Button>
      <Button
        variant="outlined"
        fullWidth
        onClick={() => navigate(`/mypage/${user.id}`)}
        color="secondary">
        마이페이지로 돌아가기
      </Button>
    </Box>
  );
};

export default PetRegisterButtons;