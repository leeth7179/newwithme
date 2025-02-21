import { createSlice } from "@reduxjs/toolkit";
import { fetchWithAuth } from "../common/fetchWithAuth.js";
import { API_URL } from "../constant";

const authSlice = createSlice({
  name: "auth", // name: 슬라이스 이름, 이 슬라이스는 Redux 상태에서 "auth"라는 이름의 상태 부분을 관리함
  initialState: {
    // initialState: 슬라이스의 초기 상태, 사용자 정보와 로그인 여부를 저장
    user: null, // user: 사용자 정보 상태 값, 초기값은 null
    isLoggedIn: false // isLoggedIn: 로그인 여부 상태 값, 초기값은 false
  },
  reducers: {
    // reducers: 상태를 변경하는 함수를 정의
    setUser(state, action) {
      // 1. 첫번째 리듀서 함수, 사용자 정보를 저장, state: 현재 상태, action: 액션 객체로 type과 payload를 포함. 이렇게 전달된 payload에서 값을 추출하여 사용자 정보를 변경
      state.user = action.payload; // name, email. roles 정보가 user 상태에 저장(객체)
      state.isLoggedIn = true;
    },
    clearUser(state) {
      // 2. 두번째 리듀서 함수, 사용자 정보를 초기화, state: 현재 상태, action: 없음. 사용자 정보를 초기화하고 로그인 상태를 false로 변경
      state.user = null;
      state.isLoggedIn = false;
    }
  }
});

export const fetchUserInfo = () => async (dispatch) => {
 try {
   const response = await fetchWithAuth(`${API_URL}auth/userInfo`);
   if (!response.ok) {
     if (response.status === 401) {
       console.warn("인증되지 않은 사용자 요청입니다.");
       return; // 401 상태일 때 상태 초기화 없이 종료
     }
     throw new Error("사용자 정보 가져오기 실패");
   }
   const userData = await response.json();
   console.log("fetchUserInfo 사용자 정보 userData : ", userData);

   // 사용자 정보가 없는 경우 경고를 출력하고 초기화는 수행하지 않음
   if (!userData || Object.keys(userData).length === 0) {
     console.warn("존재하지 않는 사용자 정보입니다.");
     return;
   }

   console.log("사용자 정보:", userData);

   dispatch(setUser(userData)); // 응답 결과로 Redux 상태를 변경 하기 위해 setUser 액션을 디스패치

   // 사용자 정보 로드 후 메시지 데이터도 함께 로드
   const messagesResponse = await fetchWithAuth(`${API_URL}messages/received/${userData.id}`);
   if (messagesResponse.ok) {
     const messages = await messagesResponse.json();
     dispatch(setMessages(messages));

     // 읽지 않은 메시지 수 설정
     const unreadCount = messages.filter(msg => !msg.read).length;
     dispatch(setUnreadCount(unreadCount));
   }

 } catch (error) {
   //console.error("사용자 정보 가져오기 오류:", error.message);
   dispatch(clearUser()); // 오류 시 사용자 정보 초기화
 }
};

export const { setUser, clearUser } = authSlice.actions; // 액션 생성자 함수 내보내기

export default authSlice.reducer;
