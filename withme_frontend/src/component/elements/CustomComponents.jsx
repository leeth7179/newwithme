import React from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Checkbox from "@mui/material/Checkbox";
import Radio from "@mui/material/Radio";
import Dialog from "@mui/material/Dialog";
import Tooltip from "@mui/material/Tooltip";
import Snackbar from "@mui/material/Snackbar";
import { styled } from "@mui/material/styles";
import theme from "../../assets/css/mui/theme"; // 테마 가져오기

// PrimaryButton 스타일 정의
export const PrimaryButton = styled(Button)({
  boxShadow: "none",
  textTransform: "none",
  fontSize: 16,
  padding: "6px 12px",
  border: "1px solid",
  lineHeight: 1.5,
  backgroundColor: theme.palette.primary.main, // theme에서 색상 가져오기
  borderColor: theme.palette.primary.main,
  color: theme.palette.primary.contrastText,
  "&:hover": {
    backgroundColor: theme.palette.primary.dark,
    borderColor: theme.palette.primary.dark,
    boxShadow: "none"
  },
  "&:active": {
    boxShadow: "none",
    backgroundColor: theme.palette.primary.light,
    borderColor: theme.palette.primary.light
  },
  "&:focus": {
    outline: `2px solid rgba(${theme.palette.primary.main},0.4)`
  }
});

// OutlineButton 스타일 정의
export const OutlineButton = styled(Button)({
  boxShadow: "none",
  textTransform: "none",
  fontSize: 16,
  padding: "6px 12px",
  border: "1px solid",
  lineHeight: 1.5,
  backgroundColor: theme.palette.tertiary.light,
  borderColor: theme.palette.primary.main,
  color: theme.palette.primary.dark,
  "&:hover": {
    backgroundColor: theme.palette.secondary.main,
    borderColor: theme.palette.secondary.main,
    color: theme.palette.secondary.contrastText,
    boxShadow: "none"
  }
});

// TextButton 스타일 정의
export const TextButton = styled(Button)({
  boxShadow: "none",
  textTransform: "none",
  fontSize: 16,
  padding: "6px 12px",
  border: "1px solid",
  lineHeight: 1.5,
  backgroundColor: theme.palette.tertiary.light,
  borderColor: theme.palette.tertiary.light,
  color: theme.palette.primary.dark,
  "&:hover": {
    backgroundColor: theme.palette.secondary.main,
    borderColor: theme.palette.secondary.main,
    color: theme.palette.secondary.contrastText,
    boxShadow: "none"
  }
});

// ToggleButton 스타일 정의
export const ToggleButton = styled(({ isActive, ...props }) => (
  <Button {...props} />
))(({ isActive, theme }) => ({
  backgroundColor: isActive
    ? theme.palette.primary.main
    : theme.palette.surface.light,
  color: isActive
    ? theme.palette.primary.contrastText
    : theme.palette.text.secondary,
  border: `1px solid ${theme.palette.primary.main}`,
  "&:hover": {
    backgroundColor: isActive
      ? theme.palette.primary.dark
      : theme.palette.surface.main
  }
}));

// FloatingActionButton 스타일 정의
export const FloatingActionButton = styled(Button)({
  backgroundColor: theme.palette.primary.main,
  color: theme.palette.primary.contrastText,
  borderRadius: "50%",
  width: "56px",
  height: "56px",
  boxShadow: "0px 4px 6px rgba(0,0,0,0.1)",
  "&:hover": {
    backgroundColor: theme.palette.primary.dark
  }
});

// BorderedButton 스타일 정의
export const BorderedButton = styled(Button)({
  backgroundColor: "transparent",
  color: theme.palette.primary.main,
  border: `1px solid ${theme.palette.primary.main}`,
  "&:hover": {
    backgroundColor: `rgba(${theme.palette.primary.main},0.12)`,
    color: theme.palette.text.secondary
  }
});

// DeleteButton 스타일 정의
export const DeleteButton = styled(Button)({
  boxShadow: "none",
  textTransform: "none",
  fontSize: 16,
  padding: "6px 12px",
  border: "1px solid",
  lineHeight: 1.5,
  backgroundColor: theme.palette.error.main,
  borderColor: theme.palette.error.main,
  color: "#fff",
  "&:hover": {
    backgroundColor: theme.palette.error.dark,
    borderColor: theme.palette.error.dark,
    boxShadow: "none"
  }
});

// CustomTextField 정의
export const CustomTextField = styled(TextField)({
  "& .MuiInputBase-root": {
    borderRadius: "8px",
    backgroundColor: theme.palette.surface.light,
    padding: "10px"
  },
  "& .MuiOutlinedInput-notchedOutline": {
    borderColor: theme.palette.primary.main
  },
  "& .MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline": {
    borderColor: theme.palette.primary.dark
  },
  "& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline": {
    borderColor: theme.palette.primary.dark,
    borderWidth: "2px"
  },
  "& .MuiInputBase-input": {
    color: theme.palette.text.primary
  }
});

// CustomCheckbox 정의
export const CustomCheckbox = styled(Checkbox)({
  color: theme.palette.primary.main,
  "&.Mui-checked": {
    color: theme.palette.primary.dark
  },
  "&:hover": {
    backgroundColor: "rgba(252,141,77,0.12)"
  }
});

// CustomRadioButton 정의
export const CustomRadioButton = styled(Radio)({
  color: theme.palette.secondary.main,
  "&.Mui-checked": {
    color: theme.palette.secondary.dark
  },
  "&:hover": {
    backgroundColor: "rgba(166,90,58,0.12)"
  }
});

// Dialog
export const CustomDialog = styled(Dialog)({
  "& .MuiPaper-root": {
    backgroundColor: theme.palette.surface.main,
    padding: "20px",
    borderRadius: "12px"
  }
});

// Tooltip
export const CustomTooltip = styled(({ className, ...props }) => (
  <Tooltip {...props} classes={{ popper: className }} />
))({
  "& .MuiTooltip-tooltip": {
    backgroundColor: theme.palette.primary.main,
    color: theme.palette.primary.contrastText,
    fontSize: "14px",
    padding: "10px",
    borderRadius: "4px"
  }
});

// Snackbar
export const CustomSnackbar = styled(Snackbar)({
  "& .MuiSnackbarContent-root": {
    backgroundColor: theme.palette.secondary.main,
    color: theme.palette.secondary.contrastText,
    fontSize: "16px",
    borderRadius: "8px",
    boxShadow: "0px 4px 6px rgba(0,0,0,0.1)"
  }
});
