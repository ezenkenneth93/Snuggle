import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

export default function FeedbackPage() {
  const location = useLocation();
  const { homeworkId } = location.state || {};

  const [homeworkData, setHomeworkData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // ✅ 숙제 단일 조회 API 호출
  useEffect(() => {
    if (!homeworkId) {
      setError('유효하지 않은 접근입니다.');
      setLoading(false);
      return;
    }

    const fetchHomework = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(`/api/homeworks/${homeworkId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setHomeworkData(response.data);
      } catch (err) {
        setError('숙제를 불러오는 데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchHomework();
  }, [homeworkId]);

  // ✅ 로딩 중
  if (loading) {
    return (
      <div className="min-h-screen flex justify-center items-center text-green-700 text-xl">
        로딩 중...
      </div>
    );
  }

  // ✅ 에러 발생
  if (error) {
    return (
      <div className="min-h-screen flex justify-center items-center text-red-600 font-semibold">
        {error}
      </div>
    );
  }

  // ✅ 정상 데이터 렌더링
return (
    <div className="min-h-screen bg-yellow-50">
      <div className="flex flex-col items-center px-4 py-10">
        <div className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl p-8">
          <h2 className="text-3xl font-extrabold text-green-700 mb-8 text-center">피드백 결과</h2>

          <div className="space-y-8">
            {/* 1. 제출한 숙제 */}
            <section className="bg-gray-50 p-6 rounded-lg border-l-4 border-green-600 shadow-inner">
              <h3 className="text-2xl font-semibold text-gray-800 mb-2">📚 제출한 숙제</h3>
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {homeworkData.userHomework || '제출한 숙제가 없습니다.'}
              </p>
            </section>

            {/* 2. AI 피드백 */}
            <section className="bg-gray-50 p-6 rounded-lg border-l-4 border-yellow-500 shadow-inner">
              <h3 className="text-2xl font-semibold text-gray-800 mb-2">🤖 AI 피드백</h3>
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {homeworkData.aiFeedback || '아직 피드백 기능 미구현'}
              </p>
            </section>

            {/* 3. 나의 질문 */}
            <section className="bg-gray-50 p-6 rounded-lg border-l-4 border-blue-500 shadow-inner">
              <h3 className="text-2xl font-semibold text-gray-800 mb-2">💬 나의 질문</h3>
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {homeworkData.userQuestion || '질문이 없습니다.'}
              </p>
            </section>

            {/* 4. AI 답변 */}
            <section className="bg-gray-50 p-6 rounded-lg border-l-4 border-green-700 shadow-inner">
              <h3 className="text-2xl font-semibold text-gray-800 mb-2">📝 AI 답변</h3>
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {homeworkData.aiAnswer || '아직 AI답변기능 미구현'}
              </p>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
}
