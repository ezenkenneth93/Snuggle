import React from 'react';
import { useEffect, useState } from 'react';

export default function HomePage() {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  // 스크롤에 따라 이미지 인덱스를 순환
  // useEffect(() => {
  //   const handleScroll = () => {
  //     const newIndex = Math.floor(window.scrollY / 300) % images.length;
  //     setCurrentImageIndex(newIndex);
  //   };

  //   window.addEventListener('scroll', handleScroll);
  //   return () => window.removeEventListener('scroll', handleScroll);
  // }, []);

  const image = '/img/snuggle_sheep.png'


  return (
    <div className="font-sans bg-yellow-50 min-h-screen">

      {/* Hero Section */}
      <div
        className="w-full h-[600px] bg-contain bg-no-repeat bg-center"
        style={{ backgroundImage: `url(${image})` }}
      ></div>

      {/* 텍스트 아래 영역 */}
      <div className="bg-yellow-100 py-16 text-center">
        <h1 className="text-5xl font-bold text-green-700">숙제 했니?</h1>
        <p className="mt-4 text-lg text-gray-600">선생님 좀 보여줄래?</p>
      </div>

      {/* 아래 더미 콘텐츠로 스크롤 생성 */}
      {/* <div className="h-[1200px] bg-yellow-200"></div> */}

    </div>
  );
}
