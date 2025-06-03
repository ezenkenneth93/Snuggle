import React from 'react';

export default function FeedbackPage() {
  // 예시 데이터 (실제 연동 시 API 호출)
  const sampleHomework = 'My day in English: Today I went to the park and enjoyed the sunshine.';
  const sampleScore = 92;
  const sampleScoreFeedback = '전반적으로 문법과 어휘 사용이 훌륭합니다. 다만 시제 일관성을 조금 더 신경 써 보세요.';
  const sampleImproved = `Today, I visited the park to bask in the warm sunshine and appreciate the serene atmosphere.`;

  return (
    <div className="min-h-screen bg-yellow-50">

      <div className="flex flex-col items-center px-4 py-10">
        <div className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl p-8">
          <h2 className="text-3xl font-extrabold text-green-700 mb-8 text-center">피드백 결과</h2>

          <div className="space-y-8">
            {/* 제출한 숙제 */}
            <section className="bg-gray-50 p-6 rounded-lg border-l-4 border-green-600 shadow-inner">
              <h3 className="text-2xl font-semibold text-gray-800 mb-2">📚 제출한 숙제</h3>
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">{sampleHomework}</p>
            </section>

            {/* AI 피드백 (채점) */}
            <section className="bg-gray-50 p-6 rounded-lg border-l-4 border-yellow-500 shadow-inner">
              <h3 className="text-2xl font-semibold text-gray-800 mb-2">🤖 AI 피드백 (채점)</h3>
              <p className="text-lg text-green-600 font-bold">점수: {sampleScore}점</p>
              <p className="mt-2 text-gray-700 leading-relaxed">{sampleScoreFeedback}</p>
            </section>

            {/* 더 나은 표현 */}
            <section className="bg-gray-50 p-6 rounded-lg border-l-4 border-green-700 shadow-inner">
              <h3 className="text-2xl font-semibold text-gray-800 mb-2">📝 AI가 추천하는 더 나은 표현</h3>
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">{sampleImproved}</p>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
}
