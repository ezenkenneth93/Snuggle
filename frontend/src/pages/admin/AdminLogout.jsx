import React, { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * 관리자 로그아웃 컴포넌트
 * - adminToken 제거 및 상태 업데이트 후 로그인 페이지로 이동
 */
export default function AdminLogout({ setIsAdminLoggedIn }) {
  const navigate = useNavigate();
  const hasLoggedOut = useRef(false);

  useEffect(() => {
    if (!hasLoggedOut.current) {
      hasLoggedOut.current = true;

      // 관리자 토큰 제거
      localStorage.removeItem('adminToken');
      // 로그인 상태 업데이트
      setIsAdminLoggedIn(false);
      alert('관리자 로그아웃 되었습니다.');
      // 로그인 페이지로 이동
      navigate('/admin/login');
    }
  }, [navigate, setIsAdminLoggedIn]);

  return null;
}
