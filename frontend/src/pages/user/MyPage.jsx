import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function MyPage() {
  const [selectedDate, setSelectedDate] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);    
  const [homeworkData, setHomeworkData] = useState(null);   //숙제
  const [isLoading, setIsLoading] = useState(false);  // 로딩중인가?
  const [userId, setUserId] = useState(null);         // 조회를 위한 userId
  const [totalCount, setTotalCount] = useState(0);    // 총 제출일수
  const [streakCount, setStreakCount] = useState(0);  // 연속 제출일수
  const [myRank, setMyRank] = useState(null);         // 순위

  // 추가: 제출된 날짜 목록 상태
  const [submittedDates, setSubmittedDates] = useState([]);
  const [datesError, setDatesError] = useState(null);

  // 내 순위 가져오기
  useEffect(() => {
    const fetchMyRank = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get("/api/homeworks/rank/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setMyRank(res.data.rank);
      } catch (err) {
        console.error("개인 랭킹 조회 실패:", err);
        setMyRank(null);
      }
    };

    fetchMyRank();
  }, []);


  // 연속 제출일수 가져오기
  useEffect(() => {
    if (!userId) return;

    const fetchStreak = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(`/api/users/${userId}/homework/streak`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setStreakCount(res.data);
      } catch (err) {
        console.error("연속 제출 일수 조회 실패:", err);
      }
    };

    fetchStreak();
  }, [userId]);

  // 유저 정보 가져오기
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get("/api/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserId(res.data.userId);  // ✅ 바로 여기서 userId 추출
      } catch (err) {
        console.error("유저 정보 가져오기 실패:", err);
      }
    };

    fetchUserInfo();
  }, []);

  // 위에서 가져온 userId로 해당 회원이 몇번 숙제 제출했는지
  useEffect(() => {
    const fetchCount = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(`/api/users/${userId}/homework/count`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setTotalCount(res.data);
      } catch (err) {
        console.error("제출 횟수 조회 실패:", err);
      }
    };
    fetchCount();
  }, [userId]);


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
      <div className="flex flex-col items-center py-12 space-y-6">
        {/* 숙제 제출 횟수, 연속, 순위 */}
        <div className="mb-6">
          <div className="bg-white shadow-md rounded-2xl px-8 py-6 border-l-8 border-green-400 transition-transform hover:scale-[1.01]">
            <h2 className="text-2xl font-bold text-blue-700 mb-2">🏅 이번 달 나의 순위:</h2>
            <p className="text-gray-800 text-lg">
              현재 <span className="font-extrabold text-blue-600 text-xl">{myRank ?? '계산 중...'}</span>위예요!
            </p>
            <br/><br/>

            <h2 className="text-2xl font-bold text-yellow-700 mb-2">🔥 연속 제출 일수:</h2>
            <p className="text-gray-800 text-lg">
              현재 <span className="font-extrabold text-yellow-600 text-xl">{streakCount}</span>일 연속으로 제출 중이에요!
            </p>
            <br/><br/>

            <h2 className="text-2xl font-bold text-green-700 mb-2">📊 숙제 제출 현황:</h2>
            <p className="text-gray-800 text-lg">
              지금까지 총 <span className="font-extrabold text-green-600 text-xl">{totalCount}</span> 회 제출했어요!
            </p>

          </div>
        </div>

        {/* 연속 제출 일수 카드
        <div className="bg-white shadow-md rounded-2xl px-8 py-6 border-l-8 border-yellow-400">
          <h2 className="text-2xl font-bold text-yellow-700 mb-2">🔥 연속 제출 일수</h2>
          <p className="text-gray-800 text-lg">
            현재 <span className="font-extrabold text-yellow-600 text-xl">{streakCount}</span>일 연속으로 제출 중이에요!
          </p>
        </div> */}
      

        {/* 달력 */}
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
