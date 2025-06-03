import React from 'react';

export default function SubmitForm() {
  return (
    <div className="min-h-screen bg-yellow-50">
      <div className="flex flex-col items-center px-4 py-10">
        {/* 마스코트 이미지 */}
        <img
          src="/img/snuggle_sheep.png"
          alt="Mascot"
          className="w-48 h-auto mb-6 drop-shadow-lg"
        />

        {/* 폼 박스 */}
        <div className="bg-white rounded-2xl shadow-md w-full max-w-lg p-8">
          <h2 className="text-2xl font-bold text-center text-green-700 mb-6">숙제 제출</h2>

          <form className="space-y-6">
            <div>
              <label htmlFor="homework" className="block text-sm font-medium text-gray-700">
                숙제 영작하기
              </label>
              <textarea
                id="homework"
                rows="4"
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-green-500 focus:border-green-500"
                placeholder="여기에 숙제를 작성하세요."
              ></textarea>
            </div>

            <div>
              <label htmlFor="question" className="block text-sm font-medium text-gray-700">
                질문하기
              </label>
              <textarea
                id="question"
                rows="4"
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-green-500 focus:border-green-500"
                placeholder="궁금한 내용을 입력하세요."
              ></textarea>
            </div>

            <button
              type="submit"
              className="w-full bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700 transition"
            >
              제출하기
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
