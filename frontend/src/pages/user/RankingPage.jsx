import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function RankingPage() {
  const [ranking, setRanking] = useState([]);

  useEffect(() => {
    const fetchRanking = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get("/api/homeworks/rank", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setRanking(res.data);
      } catch (err) {
        console.error("랭킹 불러오기 실패:", err);
      }
    };

    fetchRanking();
  }, []);

return (
  <div className="min-h-screen bg-gray-100 py-12 px-4">
    <h1 className="text-3xl font-bold text-center text-green-700 mb-8">
      📊 이번 달 숙제 제출 랭킹
    </h1>

    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6 max-w-7xl mx-auto">
      {ranking.map((user) => (
        <div
          key={user.userId}
          className="bg-white rounded-2xl shadow-md p-4 flex flex-col justify-between h-36"
        >
          <div>
            <p className="text-lg font-semibold">🏅 {user.rank}위</p>
            <p className="text-gray-700 text-sm">👤 {user.userName}</p>
            <p className="text-gray-700 text-sm">📱 {user.phoneNumber}</p>
          </div>
          <div className="text-right text-green-600 text-lg font-bold">
            {user.count}회
          </div>
        </div>
      ))}
    </div>
  </div>
);

}
