import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function AdminLoginPage({ setIsAdminLoggedIn }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/admin/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      if (response.ok) {
        const token = await response.text();
        localStorage.setItem('adminToken', token);
        setIsAdminLoggedIn(true);        // ← 이 줄 추가!
        alert('로그인 성공!');
        navigate('/admin/dashboard');
      } else {
        alert('로그인 실패');
      }
    } catch (err) {
      console.error('관리자 로그인 오류:', err);
      alert('로그인 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-yellow-50 flex flex-col justify-center items-center px-4">
      <img
        src="/img/snuggle_sheep.png"
        alt="Snuggly Mascot"
        className="w-48 h-auto mb-6 drop-shadow-lg"
      />
      <div className="bg-white rounded-2xl shadow-md w-full max-w-sm p-8">
        <h2 className="text-2xl font-bold text-center text-green-700 mb-6">관리자 로그인</h2>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="username" className="block text-sm font-medium text-gray-700">아이디</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-green-500 focus:border-green-500"
              placeholder="admin1"
              required
            />
          </div>
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">비밀번호</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-green-500 focus:border-green-500"
              placeholder="••••••••"
              required
            />
          </div>
          <button
            type="submit"
            className="w-full bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700 transition"
          >
            로그인
          </button>
        </form>
      </div>
    </div>
  );
}