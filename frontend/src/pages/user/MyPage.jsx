import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function MyPage() {
  const [selectedDate, setSelectedDate] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [homeworkData, setHomeworkData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // 추가: 제출된 날짜 목록 상태
  const [submittedDates, setSubmittedDates] = useState([]);
  const [datesError, setDatesError] = useState(null);

  // 마운트 시 제출된 날짜 목록 가져오기
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setDatesError('로그인이 필요합니다.');
      return;
    }
    axios
      .get('/api/homeworks/me/dates', {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(res => {
        // 문자열 배열을 Date 객체 배열로 변환
        const dates = res.data.map(d => new Date(d));
        setSubmittedDates(dates);
      })
      .catch(err => {
        console.error('제출 날짜 조회 실패:', err);
        setDatesError('제출 날짜 로딩 실패');
      });
  }, []);

  const handleDateSelect = async date => {
    if (!date) return;

    setSelectedDate(date);
    setIsModalOpen(true);
    setIsLoading(true);
    setHomeworkData(null);

    const formattedDate = date.toLocaleDateString('sv-SE');
    const token = localStorage.getItem('token');

    try {
      const response = await axios.get(
        `/api/homeworks/me/date?date=${formattedDate}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setHomeworkData(response.data);
    } catch (error) {
      console.error('숙제 불러오기 실패:', error);
      setHomeworkData(null);
    } finally {
      setIsLoading(false);
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setHomeworkData(null);
  };

  return (
    <div className="min-h-screen bg-yellow-50">
      <div className="flex justify-center py-12">
        <div className="bg-white rounded-xl shadow-xl p-8">
         <DayPicker
  mode="single"
  selected={selectedDate}
  onSelect={handleDateSelect}
  className="!bg-white !p-2"
  // 모든 날짜(day)에 hover 스타일 적용
  classNames={{
    day: 'hover:bg-green-500 hover:text-white',
    selected: 'none',
    submitted: 'text-orange-500 rounded-full w-5 h-5 ring-2 ring-indigo-300 text-center leading-tight',
    today: 'border-2 border-green-500 text-green-800',
  }}
  modifiers={{ submitted: submittedDates }}
  modifiersClassNames={{
    submitted: 'text-orange-500 rounded-full w-5 h-5 ring-2 ring-indigo-300 text-center leading-tight',
    today: 'border-2 border-green-500 text-green-800',
  }}
/>

          {datesError && <p className="text-center text-red-500 mt-2">{datesError}</p>}
        </div>
      </div>

      {/* 모달 */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 transition-opacity">
          <div className="bg-white rounded-3xl shadow-2xl w-full max-w-5xl p-10 mx-4 transform scale-95 animate-fade-in">
            <div className="flex justify-between items-center mb-8">
              <h2 className="text-3xl font-extrabold text-green-700">
                {selectedDate?.toLocaleDateString()} 기록 보기
              </h2>
              <button
                onClick={closeModal}
                className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
              >
                ×
              </button>
            </div>

            {isLoading ? (
              <div className="text-center text-gray-500 py-10 text-lg animate-pulse">
                불러오는 중...
              </div>
            ) : homeworkData ? (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="md:col-span-1 space-y-4">
                  {[
                    { title: '📚 내 숙제', data: homeworkData.userHomework || '제출된 숙제가 없습니다.', border: 'border-green-600' },
                    { title: '📝 내 질문', data: homeworkData.userQuestion || '질문을 하지 않으셨습니다.', border: 'border-yellow-500' },
                  ].map((item, idx) => (
                    <div
                      key={idx}
                      className={`bg-white rounded-xl shadow-lg p-6 border-l-4 ${item.border} transition-transform hover:scale-[1.02] h-[200px] overflow-y-auto`}
                    >
                      <h3 className="text-xl font-semibold text-gray-800 mb-3">{item.title}</h3>
                      <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">{item.data}</p>
                    </div>
                  ))}
                </div>
                <div className="md:col-span-2">
                  <div className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-green-600 transition-transform hover:scale-[1.01] h-[420px] overflow-y-auto">
                    <h3 className="text-xl font-semibold text-gray-800 mb-3">💬 피드백</h3>
                    <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                      {homeworkData.aiFeedback || '아직 피드백이 없습니다.'}
                    </p>
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-red-600 text-center text-lg">해당 날짜의 숙제가 없습니다.</div>
            )}

            <div className="text-center mt-10">
              <button
                onClick={closeModal}
                className="bg-green-600 text-white px-8 py-3 rounded-full font-semibold hover:bg-green-700 transition"
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      )}

      <style>{`
        @keyframes fadeIn {
          0% { opacity: 0; transform: scale(0.95); }
          100% { opacity: 1; transform: scale(1); }
        }

        .animate-fade-in {
          animation: fadeIn 0.3s ease-out forwards;
        }
      `}</style>
    </div>
  );
}
