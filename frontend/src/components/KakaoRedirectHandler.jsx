// src/pages/KakaoRedirectHandler.jsx
// 카카오 로그인 후 Redirect URI로 이동했을 때 실행되는 컴포넌트
// URL에 포함된 code를 백엔드로 전달해서 로그인 처리

import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const KakaoRedirectHandler = ({ setIsLoggedIn }) => {
  const navigate = useNavigate();   // 페이지 이동을 위한 Hook

  useEffect(() => {
    // 현재 브라우저 URL에서 code 파라미터 추출
    const code = new URL(window.location.href).searchParams.get("code");

    // 받아온 인가코드 출력
    console.log("카카오 인가코드(code):", code);
    
    if (code) {
        // 백엔드로 인가코드 전달하여 로그인 시도
      axios.post("http://localhost:8081/api/oauth/kakao/login", 
        { code },
        {
          headers: {
            "Content-Type": "application/json",  // ✅ 필수!!
          }
        }
      )
        .then(res => {
            // 로그인 성공 → 응답 받은 JWT 토큰 저장
          localStorage.setItem("token", res.data.token); // JWT 저장
          setIsLoggedIn(true);  // ✅ 이렇게 직접 알려줘야 React가 즉시 반응함
          alert("로그인 성공!")
          navigate("/");    // 홈 화면으로 이동
        })
        .catch(() => {

        // 로그인 실패 (ex. DB에 전화번호 없는 경우) → 추가정보 입력 페이지로 이동
          navigate("/extra-info"); 
        });
    }
  }, []);

  return <div>로그인 중입니다...</div>;
};

export default KakaoRedirectHandler;
