/**
 * src/common/fetchWithAuth.js
 * - API 요청 시 JWT 액세스 토큰을 헤더에 포함하여 요청시 사용
 * - fetch API를 사용하여 API 요청을 보내고, 응답을 반환
 * - fetch API의 두 번째 인자로 옵션 객체를 받아서 사용
 * - 기본 Content-Type은 application/json으로 설정
 * - 액세스 토큰은 저장시 "/"로 저장해서 모든 요청에 포함되며 리프레시 토큰은 "/refresh"로 저장해서 "/refresh" 요청에만 포함됨
 * - 401 UnAuthroized 상태 발생 시 "/refresh" 요청을 서버에 하게 되면 액세스 토큰+리프레시 토큰이 함께 서버로 전성됨.
 *   전송된 리프레시 토큰은 서버에서 검증 후 새로운 액세스 토큰을 발급하고 이를 응답으로 클라이언트에 전송함.
 */

import { SERVER_URL } from "../constant"; // "/refresh" 요청 시 사용

/**
 * 액세스 토큰 갱신 함수
 * - 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받는 함수
 * - API_URL/auth/refresh 엔드포인트로 POST 요청을 보내서 새로운 액세스 토큰을 발급받음
 * - 응답이 성공하면 새로운 액세스 토큰을 반환
 *
 * @returns {Promise<null|*>}
 */
export const refreshAccessToken = async () => {
    try {
        // 1. 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받는 함수, fetch API의 응답은 Promise 객체로 반환된다.
        const response = await fetch(`${SERVER_URL}refresh`, {
            method: "POST",
            credentials: "include", // HttpOnly 쿠키를 포함해서 요청
            headers: {
                "Content-Type": "application/json",
            },
        });
        console.log("refreshAccessToken /refresh 요청후 받은 응답 response: ", response);

        if (!response.ok) {
            console.log("리프레시 토큰 갱신 실패", response.status);
            return false;
        }

        console.log("refreshAccessToken 리프레시 토큰 발급 성공");
        return true; // 성공 시 true 반환

    } catch (error) {
        console.error("리프레시 토큰 처리 오류:", error.message);
        return false; // 실패 여부 반환
    }
};


/**
 * API 요청을 보내는 함수
 * - 요청을 보낼 때 헤더와 JWT 토큰을 포함하여 요청
 * - options 객체에 method, body 등을 객체 형태로 만들어서 보내기 때문에 options = {}로 초기화
 * - credentials: "include" : 요청할 때 HttpOnly 쿠키를 포함해서 요청, 이게 있어야 서버에서 인증.
 *   서버에서는 이걸 받아서 토큰을 디코딩 해서 사용자 정보를 추출하고 그것을 SecurityContext 에 저장한다.
 *   그리고 저장된 정보에서 권한을 조회해서 요청한 메뉴에 대한 권한이 있는지 확인한다.
 * @param {string} url 요청할 URL
 * @param {Object} options fetch API의 두 번째 인자로 전달할 옵션 객체
 */
/*export const fetchWithAuth = async (url, options = {}) => {
    const config = {
        ...options,
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    };

    try {
        // 1. 서버로 첫 번째 요청, fetch API의 반환 결과는 Promise 객체로 반환되며, response 객체를 반환한다. response 객체는 응답에 대한 정보를 담고 있다. 예를 들면 응답이 성공이면 response.ok는 true이다.
        let response = await fetch(url, config);

        // 2. 1.번 요청 결과 401 Unauthorized 상태 처리가 반환 되었을 때
        if (response.status === 401) {
            const errorData = await response.json();
            console.warn(`401 Error: ${errorData.message}`);

            if (errorData.message.includes("만료")) {
                // 액세스 토큰 만료: 리프레시 토큰으로 액세스 토큰 갱신 시도
                console.log("fetchWithAuth.js: 액세스 토큰 만료되어 refreshAccessToken() 호출 - 1");
                const refreshSuccess = await refreshAccessToken();

                if (refreshSuccess) {
                    console.log("리프레시 토큰 성공, 기존 요청 재시도");
                    response = await fetch(url, config); // 기존 요청 재시도
                } else {
                    console.error("리프레시 토큰 갱신 실패");
                    throw new Error("Unauthorized: 리프레시 토큰 갱신 실패");
                }
            } else {
                throw new Error(`Unauthorized: ${errorData.message}`);
            }
        }

        // 3. 정상 응답 반환
        return response;
    } catch (error) {
        console.error("API 요청 실패:", error.message);
        throw error;
    }
};*/

export const fetchWithAuth = async (url, options = {}) => {
    const config = {
        ...options,
        headers: {
            ...options.headers, // 기존 헤더 유지
        },
        credentials: "include",
    };

    // FormData를 사용하는 경우 Content-Type 헤더를 설정하지 않음
    if (!(options.body instanceof FormData)) {
        config.headers["Content-Type"] = "application/json";
    }

    try {
        // 1. 서버로 첫 번째 요청, fetch API의 반환 결과는 Promise 객체로 반환되며, response 객체를 반환한다. response 객체는 응답에 대한 정보를 담고 있다. 예를 들면 응답이 성공이면 response.ok는 true이다.
                let response = await fetch(url, config);

                // 2. 1.번 요청 결과 401 Unauthorized 상태 처리가 반환 되었을 때
                if (response.status === 401) {
                    const errorData = await response.json();
                    console.warn(`401 Error: ${errorData.message}`);

                    if (errorData.message.includes("만료")) {
                        // 액세스 토큰 만료: 리프레시 토큰으로 액세스 토큰 갱신 시도
                        console.log("fetchWithAuth.js: 액세스 토큰 만료되어 refreshAccessToken() 호출 - 1");
                        const refreshSuccess = await refreshAccessToken();

                        if (refreshSuccess) {
                            console.log("리프레시 토큰 성공, 기존 요청 재시도");
                            response = await fetch(url, config); // 기존 요청 재시도
                        } else {
                            console.error("리프레시 토큰 갱신 실패");
                            throw new Error("Unauthorized: 리프레시 토큰 갱신 실패");
                        }
                    } else {
                        throw new Error(`Unauthorized: ${errorData.message}`);
                    }
                }

        // ✅ 추가 코드: 서버 응답에서 메시지 여부 확인
        if (response.ok) {
            const clone = response.clone();
            const data = await clone.json();
            if (data && data.hasNewMessage) {
                const messageEvent = new CustomEvent('messageReceived', { detail: data.latestMessage });
                window.dispatchEvent(messageEvent);
                console.log("🔔 홈 페이지에 새 메시지 팝업 이벤트 발생");
            }
        }

        // 3. 정상 응답 반환
        return response;
    } catch (error) {
        console.error("API 요청 실패:", error.message);
        throw error;
    }
};

/**
 * 인증이 필요 없는 API 요청을 보내는 함수
 * - JWT 토큰을 포함하지 않고 요청
 * - 예를들면 회원가입, 로그인 등
 * @param url
 * @param options
 * @returns {Promise<Response>}
 */
export const fetchWithoutAuth = async (url, options = {}) => {
    const config = {
        ...options, // method, body 등 유지
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    };

    try {
        const response = await fetch(url, config); // 비동기 요청
        return response; // 서버 응답 반환
    } catch (error) {
        console.error("API 요청 실패:", error.message);
        throw error; // 오류 다시 던지기
    }
};

