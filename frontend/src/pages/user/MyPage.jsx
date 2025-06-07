import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function MyPage() {
  const [selectedDate, setSelectedDate] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [homeworkData, setHomeworkData] = useState(null);

  const handleDateSelect = async (date) => {
    if (!date) return;

    setSelectedDate(date);
    setIsModalOpen(true);

    const formattedDate = date.toLocaleDateString('sv-SE'); // 결과 예: "2025-06-06"
    const token = localStorage.getItem('token');

    try {
      const response = await axios.get(`/api/homeworks/me/date?date=${formattedDate}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setHomeworkData(response.data);
    } catch (error) {
      console.error('숙제 불러오기 실패:', error);
      setHomeworkData(null);
    }
  };

  const closeModal = () => setIsModalOpen(false);

  return (
    <div className="min-h-screen bg-yellow-50">
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

            {homeworkData ? (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div className="bg-gray-50 p-6 rounded-lg border-l-4 border-green-600 shadow-inner">
                  <h3 className="text-2xl font-bold text-gray-800 mb-3">📚 내 숙제</h3>
                  <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                    {homeworkData.userAnswer || '제출된 숙제가 없습니다.'}
                  </p>
                </div>
                <div className="bg-gray-50 p-6 rounded-lg border-l-4 border-yellow-500 shadow-inner">
                  <h3 className="text-2xl font-bold text-gray-800 mb-3">💬 피드백</h3>
                  <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                    {homeworkData.feedback || '아직 피드백이 없습니다.'}
                  </p>
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
    </div>
  );
}
