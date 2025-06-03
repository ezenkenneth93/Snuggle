import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { useEffect, useState } from "react";

import Home from './pages/Home'; 
import Login from './pages/user/Login';
import Logout from './pages/user/Logout';
import SubmitForm from './pages/user/SubmitForm';
import SubmitComplete from './pages/user/SubmitComplete';
import MyPage from './pages/user/MyPage';
import Feedback from './pages/user/Feedback';
import Navbar from "./components/Navbar";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    // 토큰 유무로 로그인 여부 판단
    const token = localStorage.getItem("token");
    setIsLoggedIn(!!token);
  }, []);

  return (
    <>
      <BrowserRouter>
        {/* ✅ Router 내부에 위치해야 Link 작동 */}
        <Navbar isLoggedIn={isLoggedIn} />
        
        <Routes>
          {/* 첫 접속시 보여질 컴포넌트트 */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login setIsLoggedIn={setIsLoggedIn} />} />

          {/* 사용자 */}
          <Route path="/submit" element={<SubmitForm />} />
          <Route path="/submit-complete" element={<SubmitComplete />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/feedback" element={<Feedback />} />
          <Route path="/logout" element={<Logout setIsLoggedIn={setIsLoggedIn} />} />

          {/* 관리자 */}
          {/* <Route path="/admin/dashboard" element={<Dashboard />} /> */}
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
