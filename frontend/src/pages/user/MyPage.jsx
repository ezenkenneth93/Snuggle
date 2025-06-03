import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import React, { useEffect, useState } from 'react';

export default function MyPage() {
  const [selectedDate, setSelectedDate] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleDateSelect = (date) => {
    if (date) {
      setSelectedDate(date);
      setIsModalOpen(true);
    }
  };
  const closeModal = () => setIsModalOpen(false);

  // 예시 데이터
  const sampleHomework = '오늘의 숙제: "My day in English" 에세이 작성';
  const sampleFeedback = '피드백: 잘 쓰셨습니다! 몇 가지 문법을 다듬으면 더 좋아요.';

  return (
    <div className="min-h-screen bg-yellow-50">
      {/* 예쁜 캘린더 박스 */}
      <div className="flex justify-center py-12">
        <div className="bg-white rounded-xl shadow-xl p-8">
          <DayPicker
            mode="single"
            selected={selectedDate}
            onSelect={handleDateSelect}
            className="!bg-white !p-2"
            modifiersClassNames={{
              selected: 'bg-green-600 text-white font-semibold',
              today: 'border-2 border-green-500 text-green-800'
            }}
          />
        </div>
      </div>

      {/* 모달창 */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-3xl shadow-2xl w-full max-w-4xl p-10 mx-4">
            <div className="flex justify-between items-center mb-8">
              <h2 className="text-3xl font-extrabold text-green-700">
                {selectedDate.toLocaleDateString()} 기록 보기
              </h2>
              <button
                onClick={closeModal}
                className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
              >
                ×
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              {/* 숙제 섹션 */}
              <div className="bg-gray-50 p-6 rounded-lg border-l-4 border-green-600 shadow-inner">
                <h3 className="text-2xl font-bold text-gray-800 mb-3">📚 내 숙제</h3>
                <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {sampleHomework}
                </p>
              </div>

              {/* 피드백 섹션 */}
              <div className="bg-gray-50 p-6 rounded-lg border-l-4 border-yellow-500 shadow-inner">
                <h3 className="text-2xl font-bold text-gray-800 mb-3">💬 피드백</h3>
                <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {sampleFeedback}
                </p>
              </div>
            </div>

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
    </div>
  );
}