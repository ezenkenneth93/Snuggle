import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

export default function AdminDashboardPage() {
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const token = localStorage.getItem('adminToken');
        const response = await axios.get('/api/admin/users', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setUsers(response.data);
      } catch (err) {
        console.error('회원 목록 로딩 오류:', err);
      }
    };
    fetchUsers();
  }, []);

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-6">회원 목록</h1>
      <table className="min-w-full bg-white">
        <thead>
          <tr>
            <th className="py-2 px-4 border-b">ID</th>
            <th className="py-2 px-4 border-b">이름</th>
            <th className="py-2 px-4 border-b">휴대폰</th>
            <th className="py-2 px-4 border-b">가입일</th>
            <th className="py-2 px-4 border-b">상세보기</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.userId} className="hover:bg-gray-100 cursor-pointer" onClick={() => navigate(`/admin/users/${user.userId}`)}>
              <td className="py-2 px-4 border-b">{user.userId}</td>
              <td className="py-2 px-4 border-b">{user.name}</td>
              <td className="py-2 px-4 border-b">{user.phoneNumber}</td>
              <td className="py-2 px-4 border-b">{new Date(user.joinDate).toLocaleDateString()}</td>
              <td className="py-2 px-4 border-b text-blue-600">자세히</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
