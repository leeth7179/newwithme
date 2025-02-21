import { createSlice } from '@reduxjs/toolkit';

const messageSlice = createSlice({
  name: 'messages',
  initialState: {
    messages: [],
    loading: false,
    unreadCount: 0,
    latestMessage: null
  },
  reducers: {
    // ✅ 전체 메시지 설정
    setMessages: (state, action) => {
      state.messages = action.payload;
      state.unreadCount = action.payload.filter(msg => !msg.isRead).length;

      // ✅ 최신 메시지가 있고 전문가의 답변일 경우에만 이벤트 발생
      const latestMsg = action.payload[action.payload.length - 1];
      if (latestMsg && latestMsg.messageType === 'answer' && latestMsg.senderRole === 'ROLE_DOCTOR') {
        window.dispatchEvent(new CustomEvent('messageReceived', {
          detail: {
            content: latestMsg.content,
            senderName: latestMsg.senderName,
            messageType: latestMsg.messageType,
            senderRole: latestMsg.senderRole
          }
        }));
      }
    },

    // ✅ 새로운 메시지 추가
    addMessage: (state, action) => {
      const newMessage = action.payload;
      const exists = state.messages.some(msg => msg.id === newMessage.id);

      if (!exists) {
        state.messages.push(newMessage);
        state.unreadCount += 1;
        state.latestMessage = newMessage;

        console.log('📬 [Redux]: 새 메시지 추가:', newMessage);

        // ✅ 전문가의 답변인 경우에만 팝업 이벤트 발생
        if (newMessage.messageType === 'answer' && newMessage.senderRole === 'ROLE_DOCTOR') {
          window.dispatchEvent(new CustomEvent('messageReceived', {
            detail: {
              content: newMessage.content,
              senderName: newMessage.senderName,
              messageType: newMessage.messageType,
              senderRole: newMessage.senderRole
            }
          }));
        }
      }
    },

    // ✅ 메시지를 읽음으로 표시
    markMessageAsRead: (state, action) => {
      const messageId = action.payload;
      const message = state.messages.find(msg => msg.id === messageId);
      if (message && !message.read) {
        message.read = true;
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }
    }
  }
});

export const { setMessages, addMessage, markMessageAsRead } = messageSlice.actions;
export default messageSlice.reducer;
