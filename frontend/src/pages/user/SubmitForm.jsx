import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function SubmitForm() {
  // ✅ 상태 정의
  const [userHomework, setUserHomework] = useState('');    // 숙제 텍스트 상태
  const [userQuestion, setUserQuestion] = useState('');    // 질문 텍스트 상태
  const [isSubmitting, setIsSubmitting] = useState(false); // 제출 중 여부 (중복 제출 방지용)
  const [submitResult, setSubmitResult] = useState(null);  // 결과 메시지 상태

  // navigate 훅 정의
  const navigate = useNavigate();

  // ✅ 제출 이벤트 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();              // 폼 기본 제출 동작(새로고침) 방지
    setIsSubmitting(true);          // 버튼 비활성화 및 로딩 UI용
    setSubmitResult(null);          // 결과 메시지 초기화

    const token = localStorage.getItem('token');  // JWT 토큰 로컬 스토리지에서 가져오기
    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    try {
      // 백엔드에 숙제 POST 요청
      const response = await axios.post(
        '/api/homeworks',           // 숙제 제출 API
        {
          userHomework,             // 바디에 들어갈 숙제 내용
          userQuestion              // 바디에 들어갈 질문 내용
        },
        {
          headers: {
            Authorization: `Bearer ${token}`  // JWT 토큰 포함
          }
        }
      );

      // ✅ 여기서 id 추출
      const homeworkId = response.data.id;

      // ✅ 이 위치에 navigate 추가
      navigate('/submit-complete', {
        state: { homeworkId }
      });

    } catch (error) {
      console.error('제출 실패:', error);
      setSubmitResult('제출 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);       // 로딩 상태 해제
    }
  };

  return (
    <div className="min-h-screen bg-yellow-50">
      <div className="flex flex-col items-center px-4 py-10">

        {/* ✅ 마스코트 이미지 */}
        <img
          src="/img/snuggle_sheep.png"
          alt="Mascot"
          className="w-48 h-auto mb-6 drop-shadow-lg"
        />

        {/* ✅ 폼 컨테이너 */}
        <div className="bg-white rounded-2xl shadow-md w-full max-w-lg p-8">
          <h2 className="text-2xl font-bold text-center text-green-700 mb-6">숙제 제출</h2>

          {/* ✅ 숙제 제출 폼 */}
          <form className="space-y-6" onSubmit={handleSubmit}>
            
            {/* 숙제 입력창 */}
            <div>
              <label htmlFor="homework" className="block text-sm font-medium text-gray-700">
                숙제 영작하기
              </label>
              <textarea
                id="homework"
                rows="4"
                value={userHomework}
                onChange={(e) => setUserHomework(e.target.value)}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-green-500 focus:border-green-500"
                placeholder="여기에 숙제를 작성하세요."
                required   // 숙제는 필수 항목
              />
            </div>

            {/* 질문 입력창 (선택 사항) */}
            <div>
              <label htmlFor="question" className="block text-sm font-medium text-gray-700">
                질문하기
              </label>
              <textarea
                id="question"
                rows="4"
                value={userQuestion}
                onChange={(e) => setUserQuestion(e.target.value)}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-green-500 focus:border-green-500"
                placeholder="궁금한 내용을 입력하세요. (선택)"
              />
            </div>

            {/* 제출 버튼 */}
            <button
              type="submit"
              className={`w-full bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700 transition ${
                isSubmitting && 'opacity-50 cursor-not-allowed'
              }`}
              disabled={isSubmitting}  // 제출 중엔 비활성화
            >
              {isSubmitting ? '제출 중...' : '제출하기'}
            </button>

            {/* 제출 결과 메시지 */}
            {submitResult && (
              <p className="text-center text-sm text-green-600 mt-4">{submitResult}</p>
            )}
          </form>
        </div>
      </div>
    </div>
  );
}
