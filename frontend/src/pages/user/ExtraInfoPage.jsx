import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const ExtraInfoPage = ({ setIsLoggedIn }) => {
  const navigate = useNavigate();
  const kakaoId = localStorage.getItem("kakaoId");

  const [name, setName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post("http://localhost:8081/api/oauth/kakao/extra-info", {
        name,
        phoneNumber,
        kakaoId
      });
      localStorage.setItem("token", res.data.token);
      setIsLoggedIn(true);
      alert("로그인 성공!");
      navigate("/");
    } catch (err) {
      if (err.response?.status === 404) {
        alert("입력하신 정보와 일치하는 회원이 없습니다.");
      } else if (err.response?.status === 409) {
        alert("이미 연결된 카카오 계정입니다.");
      } else {
        alert("오류가 발생했습니다.");
      }
    }
  };

  return (
    <div className="min-h-screen bg-yellow-50 flex flex-col justify-center items-center px-4">
      <img
        src="/img/snuggle_sheep.png"
        alt="Snuggly Mascot"
        className="w-40 h-auto mb-6 drop-shadow-lg"
      />

      <div className="bg-white rounded-2xl shadow-md w-full max-w-sm p-8">
        <h2 className="text-2xl font-bold text-center text-green-700 mb-4">추가 정보 입력</h2>
        <p className="text-sm text-gray-600 text-center mb-6">
          <span className="text-emerald-600 font-medium">스너글</span>에 등록된 <strong>이름과 전화번호</strong>를 입력해주세요.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700">이름</label>
            <input
              id="name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              className="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-emerald-400 focus:border-emerald-400"
              placeholder="예: 홍길동"
            />
          </div>

          <div>
            <label htmlFor="phone" className="block text-sm font-medium text-gray-700">전화번호</label>
            <input
              id="phone"
              type="text"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              required
              className="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-emerald-400 focus:border-emerald-400"
              placeholder="예: 01012345678"
            />
          </div>

          <button
            type="submit"
            className="w-full bg-emerald-500 hover:bg-emerald-600 text-white font-medium py-2 rounded-md transition"
          >
            제출
          </button>
        </form>
      </div>
    </div>
  );
};

export default ExtraInfoPage;
