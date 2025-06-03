import React, { useState }  from 'react';
import { useNavigate } from "react-router-dom"; // ✅ 이 줄이 필요해요

export default function Login({ setIsLoggedIn }) {
    const [phoneNumber, setPhoneNumber] = useState('');
    const [name, setName] = useState('');
    const navigate = useNavigate(); // ✅ 추가

    const handleSubmit = async (e) => {
    e.preventDefault(); // 폼 기본 제출 막기

    try {
            const res = await fetch("http://localhost:8081/api/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ phoneNumber, name })
            });

            if (res.ok) {
                const token = await res.text(); // 응답이 토큰 문자열이면
                localStorage.setItem("token", token); // ✅ 토큰 저장
                setIsLoggedIn(true); // ✅ 상태 업데이트
                alert("로그인 성공!");
                navigate("/"); // ✅ 홈으로 이동
            } else {
                alert("로그인 실패");
            }

        // 예: window.location.href = "/mypage";
        } catch (e) {
            console.error("로그인 오류:", e);
        }
    };

  return (
     <div className="min-h-screen bg-yellow-50">
      <div className="min-h-screen bg-yellow-50 flex flex-col justify-center items-center px-4">
        <img
          src="/img/snuggle_sheep.png"
          alt="Snuggly Mascot"
          className="w-48 h-auto mb-6 drop-shadow-lg"
        />

        <div className="bg-white rounded-2xl shadow-md w-full max-w-sm p-8">
          <h2 className="text-2xl font-bold text-center text-green-700 mb-6">로그인</h2>

          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label htmlFor="phone" className="block text-sm font-medium text-gray-700">휴대폰 번호</label>
              <input
                type="tel"
                id="phone"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-green-500 focus:border-green-500"
                placeholder="하이픈(-) 제외"
              />
            </div>

            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700">이름</label>
              <input
                type="text"
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-green-500 focus:border-green-500"
                placeholder="스너글"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700 transition"
            >
              로그인
            </button>
          </form>

          <p className="text-sm text-center text-gray-500 mt-6">
            계정이 없으신가요? <a href="#" className="text-green-600 hover:underline">회원가입</a>
          </p>
        </div>
      </div>
    </div>
  );
}