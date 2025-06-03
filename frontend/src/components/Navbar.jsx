import React from 'react';
import { Link } from 'react-router-dom';

/**
 * Navbar 컴포넌트
 * @param {boolean} isLoggedIn - 로그인 상태 전달
 */
export default function Navbar({ isLoggedIn }) {
  return (
    <nav className="flex items-center justify-between px-8 py-4 border-b border-gray-200 bg-white shadow-sm sticky top-0 z-50">
      {/* 로고 링크: 홈으로 이동 */}
      <div className="w-[120px]">
        <Link to="/">
          <img src="/img/snuggle_logo.png" alt="MyBrand Logo" className="h-12 object-contain" />
        </Link>
      </div>

      {/* 네비게이션 메뉴 */}
      <ul className="flex space-x-6 items-center text-gray-700 text-sm font-medium">
        {/* <li>
          <Link to="/new" className="hover:text-green-500">New</Link>
        </li>
        <li>
          <Link to="/men" className="hover:text-green-500">Men</Link>
        </li>
        <li>
          <Link to="/women" className="hover:text-green-500">Women</Link>
        </li>
        <li>
          <Link to="/kids" className="hover:text-green-500">Kids</Link>
        </li>
        <li>
          <Link to="/member-days" className="hover:text-green-500">Member Days</Link>
        </li> */}
      </ul>
        {/* 로그인 상태에 따른 추가 메뉴 */}
        {isLoggedIn ? (
          <>
            <div className="flex items-center space-x-4">
              <Link to="/mypage" className="hover:text-green-500">마이페이지</Link>
              <Link to="/logout" className="hover:text-red-500">로그아웃</Link>
            </div>            
          </>
        ) : (
          <div className="flex items-center space-x-4">
            {/* <input className="border rounded px-2 py-1 text-sm" placeholder="검색" /> */}
              <button>
                <Link to="/login" className="hover:text-green-500">로그인</Link>
              </button>
          </div>
        )}
      
    </nav>
  );
}
