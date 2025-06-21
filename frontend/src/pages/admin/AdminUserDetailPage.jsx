import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import axios from 'axios';

export default function AdminUserDetailPage() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const [homeworkData, setHomeworkData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [submittedDates, setSubmittedDates] = useState([]);

  // 회원 기본 정보 조회
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem('adminToken');
        const res = await axios.get(`/api/admin/users/${userId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setUser(res.data);
      } catch (err) {
        console.error('회원 정보 로딩 오류:', err);
      }
    };
    fetchUser();
  }, [userId]);


  // 제출 날짜 목록 가져오기
  useEffect(() => {
    const token = localStorage.getItem('adminToken');
    if (!token || !userId) return;

    axios
      .get(`/api/admin/users/${userId}/homework/dates`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(res => {
        const dates = res.data.map(date => new Date(date));
        setSubmittedDates(dates);
      })
      .catch(err => {
        console.error('제출 날짜 목록 조회 실패:', err);
      });
  }, [userId]);


  // 날짜 선택 시 해당 날짜 숙제 조회
  const handleDateSelect = async (date) => {
    if (!date) return;

    setSelectedDate(date);
    setIsModalOpen(true);
    setIsLoading(true);
    setHomeworkData(null);

    const formattedDate = date.toLocaleDateString('sv-SE');
    try {
      const token = localStorage.getItem('adminToken');
      const res = await axios.get(
        `/api/admin/users/${userId}/homeworks?date=${formattedDate}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setHomeworkData(res.data.length ? res.data[0] : null);
    } catch (err) {
      console.error('숙제 조회 오류:', err);
      setHomeworkData(null);
    } finally {
      setIsLoading(false);
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setHomeworkData(null);
  };
  
  if (!user) {
    return <div className="text-center py-20 text-gray-500">회원 정보를 불러오는 중...</div>;
  }
  return (
    <div className="min-h-screen bg-yellow-50">
      <div className="flex justify-between items-center p-6">
        <button
            type="button"
            onClick={() => navigate("/admin/dashboard")}
            className="bg-green-600 text-white px-5 py-3 rounded-full font-semibold hover:bg-green-700 transition"
        >
          ← 뒤로가기
        </button>
        {user && <h1 className="text-2xl font-extrabold mr-20">{user.name} 님의 기록</h1>}
        <div />
      </div>

      <div className="flex justify-center py-8">
        <DayPicker
          mode="single"
          selected={selectedDate}
          onSelect={handleDateSelect}
          className="!bg-white !p-4 rounded-xl shadow-lg"
          modifiers={{ submitted: submittedDates }}
          modifiersClassNames={{
            selected: 'bg-green-600 text-white font-semibold',
            today: 'border-2 border-green-500 text-green-800',
            submitted: 'text-orange-500 rounded-full w-5 h-5 ring-2 ring-indigo-300 text-center leading-tight'
          }}
        />

      </div>

      {/* 모달 */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-3xl shadow-2xl w-full max-w-5xl p-10 mx-4 animate-fade-in">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-3xl font-extrabold text-green-700">
                {selectedDate?.toLocaleDateString('ko-KR')} 기록 보기
              </h2>
              <button
                onClick={closeModal}
                className="text-gray-400 hover:text-gray-600 text-3xl font-bold"
              >×</button>
            </div>

            {isLoading ? (
              <div className="text-center text-gray-500 py-10 text-lg animate-pulse">
                불러오는 중...
              </div>
            ) : homeworkData ? (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* 왼쪽: 숙제 + 질문 */}
                <div className="md:col-span-1 space-y-4">
                  {[
                    { title: "📚 숙제", data: homeworkData.userHomework || '제출된 숙제가 없습니다.', border: "border-green-600" },
                    { title: "📝 질문", data: homeworkData.userQuestion || '질문을 하지 않으셨습니다.', border: "border-yellow-500" }
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

                {/* 오른쪽: 피드백 */}
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
              <div className="text-red-600 text-center text-lg py-10">
                해당 날짜에 제출된 숙제가 없습니다.
              </div>
            )}

            <div className="text-center mt-8">
              <button
                onClick={closeModal}
                className="bg-green-600 text-white px-8 py-3 rounded-full font-semibold hover:bg-green-700 transition"
              >닫기</button>
            </div>
          </div>
        </div>
      )}

      {/* 모달 애니메이션 */}
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
