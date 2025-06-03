// src/pages/user/Logout.jsx
import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";

export default function Logout({ setIsLoggedIn }) {
  const navigate = useNavigate();
  const hasLoggedOut = useRef(false); // ⭐ 이미 실행했는지 여부

  useEffect(() => {
    if (!hasLoggedOut.current) {
      hasLoggedOut.current = true;

      localStorage.removeItem("token");
      setIsLoggedIn(false);
      alert("로그아웃 되었습니다.");
      navigate("/");
    }
  }, [navigate, setIsLoggedIn]);

  return null; // 화면 출력 없음
}
