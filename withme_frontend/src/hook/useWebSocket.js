import { useEffect, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { addMessage } from "../redux/messageSlice";
import { showSnackbar } from "../redux/snackbarSlice";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { SERVER_URL } from "../constant";

const useWebSocket = (user) => {
  const dispatch = useDispatch();
  const stompRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const [lastMessage, setLastMessage] = useState(null);

  useEffect(() => {
    if (!user?.id) {
      console.warn('⚠️ WebSocket: 사용자 ID가 없습니다. 연결 시도 중단.');
      return;
    }

    const socket = new SockJS(`${SERVER_URL}ws`);
    stompRef.current = new Client({
      webSocketFactory: () => socket,
      debug: (msg) => console.log(`🔍 WebSocket Debug: ${msg}`),
      reconnectDelay: 5000,
      connectHeaders: {
        userId: user.id.toString()
      },

      onConnect: () => {
        console.log(`✅ WebSocket 연결 성공 - 사용자ID: ${user.id}`);
        setConnected(true);

        const topic = `/topic/chat/${user.id}`;
        stompRef.current.subscribe(topic, (message) => {
          try {
            const receivedMessage = JSON.parse(message.body);
            console.log('📩 [클라이언트]: 수신된 메시지:', receivedMessage);

            // ✅ 전문가 답변만 처리 & VIP 사용자에게만 이벤트 발생
            if (receivedMessage.messageType === 'answer' && receivedMessage.senderRole === 'ROLE_DOCTOR') {
              dispatch(addMessage(receivedMessage));
              setLastMessage(receivedMessage);

              // 🔔 VIP 사용자에게 팝업 이벤트 발생
              window.dispatchEvent(new CustomEvent('messageReceived', {
                detail: {
                  content: receivedMessage.content,
                  senderName: receivedMessage.senderName,
                  senderRole: 'ROLE_DOCTOR',
                  messageType: 'answer'
                }
              }));
              console.log('🚨 [클라이언트]: VIP messageReceived 이벤트 트리거됨');
            }
          } catch (error) {
            console.error('🚨 [클라이언트]: 메시지 파싱 실패:', error);
          }
        });
      },

      onStompError: (frame) => {
        console.error("🚨 STOMP 오류 발생:", frame.headers['message']);
        setConnected(false);
      },

      onWebSocketClose: () => {
        console.warn("⚠️ WebSocket 연결 종료됨! 재연결 시도...");
        setConnected(false);
      }
    });

    stompRef.current.activate();

    return () => {
      if (stompRef.current) {
        stompRef.current.deactivate();
        stompRef.current = null;
        setConnected(false);
        console.log("🔌 WebSocket 연결 해제 완료.");
      }
    };
  }, [user?.id, dispatch]);

  return { client: stompRef.current, connected, lastMessage };
};

export default useWebSocket;
