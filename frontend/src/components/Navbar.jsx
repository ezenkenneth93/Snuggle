import React from 'react';
import { Link } from 'react-router-dom';

/**
 * Navbar 컴포넌트
 * @param {boolean} isLoggedIn       일반 사용자 로그인 상태
 * @param {boolean} isAdminLoggedIn  관리자 로그인 상태
 */
export default function Navbar({ isLoggedIn, isAdminLoggedIn }) {
  return (
    <nav className="flex items-center justify-between px-8 py-4 border-b border-gray-200 bg-white shadow-sm sticky top-0 z-50">
      <div className="w-[120px]">
        <Link to="/">
          <img src="/img/snuggle_logo.png" alt="MyBrand Logo" className="h-12 object-contain" />
        </Link>
      </div>

      <ul className="flex space-x-6 items-center text-gray-700 text-sm font-medium">
        {/* 필요 시 추가 메뉴 */}
      </ul>

      {/* 로그인 상태에 따라 메뉴 분기 */}
      {isAdminLoggedIn ? (
        <div className="flex items-center space-x-4">
          <Link to="/admin/dashboard" className="hover:text-green-500">관리자홈</Link>
          <Link to="/admin/logout"    className="hover:text-red-500">로그아웃</Link>
        </div>
      ) : isLoggedIn ? (
        <div className="flex items-center space-x-4">
          <Link to="/ranking"   className="hover:text-green-500">랭킹</Link>
          <Link to="/submit"   className="hover:text-green-500">숙제제출</Link>
          <Link to="/mypage"   className="hover:text-green-500">마이페이지</Link>
          <Link to="/logout"   className="hover:text-red-500">로그아웃</Link>
        </div>
      ) : (
        <div className="flex items-center space-x-4">
          <Link to="/login" className="hover:text-green-500">로그인</Link>
        </div>
      )}
    </nav>
  );
}
