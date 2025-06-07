import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useLocation } from 'react-router-dom';

export default function SubmitComplete() {
  const navigate = useNavigate();
  const location = useLocation();

   // 제출 완료 페이지로 넘어올 때 전달받은 숙제 ID
  const { homeworkId } = location.state || {};  // 없을 경우를 대비한 fallback도 좋음
  
  // ✅ 유효하지 않은 접근 예외 처리
  if (!homeworkId) {
    return (
      <div className="min-h-screen flex items-center justify-center text-red-600 font-semibold">
        유효하지 않은 접근입니다. 숙제를 먼저 제출해주세요.
      </div>
    );
  }
  
  return (
    <div className="min-h-screen bg-yellow-50 flex flex-col">
      <div className="flex-grow flex flex-col justify-center items-center px-4">
        {/* 확인 아이콘 */}
        <div className="bg-green-600 text-white rounded-full p-4 mb-6">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
          </svg>
        </div>

        {/* 완료 문구 */}
        <h1 className="text-3xl font-bold text-gray-800 mb-4">제출이 완료되었습니다.</h1>
        <p className="text-gray-600 mb-8">감사합니다! 피드백을 확인하세요.</p>

        {/* 버튼 그룹 */}
        <div className="flex space-x-4">
          <button
            onClick={() => navigate('/feedback', { state: { homeworkId } })}
            className="bg-green-600 text-white px-6 py-2 rounded-md hover:bg-green-700 transition"
          >
            피드백 확인하기
          </button>
          <button
            onClick={() => navigate('/')}
            className="bg-white text-green-600 border border-green-600 px-6 py-2 rounded-md hover:bg-green-50 transition"
          >
            홈으로
          </button>
        </div>
      </div>
    </div>
  );
}
