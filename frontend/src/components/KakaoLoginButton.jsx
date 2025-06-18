// src/components/KakaoLoginButton.jsx

// ✅ 카카오 앱 REST API 키 (카카오 개발자 콘솔에서 발급받은 키)
const KAKAO_CLIENT_ID = "ed175771a7d271bb81cff7bf21400488";

// ✅ 로그인 성공 후 인가코드를 전달받을 프론트엔드 리디렉션 주소
const REDIRECT_URI = "http://localhost:3000/oauth/kakao/callback";

const KakaoLoginButton = () => {
  // ✅ 버튼 클릭 시 실행되는 로그인 함수
  const handleLogin = () => {
    // ✅ 카카오 인증 URL 구성
    const kakaoAuthUrl =
      `https://kauth.kakao.com/oauth/authorize` + // 카카오 OAuth 서버 주소
      `?client_id=${KAKAO_CLIENT_ID}` +           // 앱 REST API 키
      `&redirect_uri=${REDIRECT_URI}` +           // 리디렉션 받을 URI
      `&response_type=code`                       // 인가 코드 발급 요청
      // `&prompt=login`;                            // ✅ 이미 동의한 사용자도 매번 동의창 다시 띄움 (테스트용으로 매우 유용)

    // ✅ 생성된 URL 콘솔에 출력 (디버깅 용도)
    console.log("생성된 URL:", kakaoAuthUrl);

    // ✅ 해당 URL로 브라우저를 리디렉트 → 사용자 카카오 로그인 페이지로 이동
    window.location.href = kakaoAuthUrl;
  };

  // ✅ 버튼 UI
  return (
    <button onClick={handleLogin}>
      카카오로 로그인
    </button>
  );
};

export default KakaoLoginButton;
