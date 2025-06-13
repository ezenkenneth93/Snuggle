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
import AdminLoginPage from './pages/admin/AdminLoginPage';
import AdminDashboardPage from './pages/admin/AdminDashboardPage';
import AdminUserDetailPage from './pages/admin/AdminUserDetailPage';
import AdminLogout from './pages/admin/AdminLogout';
import RankingPage from './pages/user/RankingPage';


function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isAdminLoggedIn, setIsAdminLoggedIn] = useState(false); // ❗이 줄이 없음


  useEffect(() => {
    const userToken = localStorage.getItem("token");
    const adminToken = localStorage.getItem("adminToken");

    setIsLoggedIn(!!userToken);
    setIsAdminLoggedIn(!!adminToken); // 관리자 로그인 상태도 체크
  }, []);

  return (
    <>
      <BrowserRouter>
        {/* ✅ Router 내부에 위치해야 Link 작동 */}
        <Navbar isLoggedIn={isLoggedIn} isAdminLoggedIn={isAdminLoggedIn} /> {/* 👈 여기 */}
        
        <Routes>
          {/* 첫 접속시 보여질 컴포넌트트 */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login setIsLoggedIn={setIsLoggedIn} />} />

          {/* 사용자 */}
          <Route path="/submit" element={<SubmitForm />} />
          <Route path="/submit-complete" element={<SubmitComplete />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/feedback" element={<Feedback />} />
          <Route path="/ranking" element={<RankingPage />} />
          <Route path="/logout" element={<Logout setIsLoggedIn={setIsLoggedIn} />} />

          {/* 관리자 */}
          <Route path="/admin/login" element={<AdminLoginPage setIsAdminLoggedIn={setIsAdminLoggedIn}/>} />
          <Route path="/admin/dashboard" element={<AdminDashboardPage />}/>
          <Route path="/admin/users/:userId" element={<AdminUserDetailPage />} />
          <Route path="/admin/logout" element={<AdminLogout setIsAdminLoggedIn={setIsAdminLoggedIn} />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
