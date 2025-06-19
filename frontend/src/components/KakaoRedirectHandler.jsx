// src/pages/KakaoRedirectHandler.jsx
// 카카오 로그인 후 Redirect URI로 이동했을 때 실행되는 컴포넌트
// URL에 포함된 code를 백엔드로 전달해서 로그인 처리

import { useEffect, useState } from 'react'; // ✅ useState 추가
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const KakaoRedirectHandler = ({ setIsLoggedIn }) => {
  const navigate = useNavigate();   // 페이지 이동을 위한 Hook
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {

    // 현재 브라우저 URL에서 code 파라미터 추출
    const code = new URL(window.location.href).searchParams.get("code");

    if (!code || isProcessing) return;
    setIsProcessing(true); // ✅ 중복 요청 차단

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
          window.history.replaceState(null, "", "/oauth/kakao/callback"); // ✅ 여기서 제거
          alert("로그인 성공!")
          navigate("/");    // 홈 화면으로 이동
        })
        .catch((err) => {
          window.history.replaceState(null, "", "/oauth/kakao/callback"); // ✅ 여기서 제거

          if (err.response?.status === 404) {
            const kakaoId = err.response.data?.kakaoId; // ✅ 응답에서 kakaoId 추출
            if (kakaoId) {
              localStorage.setItem("kakaoId", kakaoId); // ✅ 저장
            }
            
            navigate("/extra-info");
            // alert("이름과 전화번호를 작성해주세요.");
          } else {
            alert(`로그인 오류: ${err.response?.data || err.message}`);
          }
        })
        .finally(() => {
          // ✅ code 제거
        });
    }
  }, [isProcessing, setIsLoggedIn, navigate]);

  return <div>로그인 중입니다...</div>;
};

export default KakaoRedirectHandler;
