import React from "react";
import PropTypes from "prop-types"; // PropTypes로 props 유효성 검사 추가
import { Box } from "@mui/material";

// TabPanel 컴포넌트 정의
const TabPanel = ({ children, value, index }) => {
  return (
    <div role="tabpanel" hidden={value !== index}>
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
};

// TabPanel의 props 유효성 검사 설정
TabPanel.propTypes = {
  children: PropTypes.node.isRequired, // children은 React 노드여야 함
  value: PropTypes.number.isRequired, // value는 숫자여야 함
  index: PropTypes.number.isRequired // index는 숫자여야 함
};

export default TabPanel;
