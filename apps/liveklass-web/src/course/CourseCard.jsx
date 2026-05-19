import React from 'react';

export function CourseCard({ course, onClick }) {
  return (
    <article className={`courseCard${onClick ? ' clickable' : ''}`}>
      <div className="courseTop">
        <span className="statusPill">{course.status}</span>
        <span className="price">{formatPrice(course.price)}</span>
      </div>
      <h2>{course.title}</h2>
      <p>{course.description}</p>
      <dl className="courseMeta">
        <div>
          <dt>기간</dt>
          <dd>{course.startDate} ~ {course.endDate}</dd>
        </div>
        <div>
          <dt>정원</dt>
          <dd>{course.capacity}명</dd>
        </div>
        <div>
          <dt>강사 ID</dt>
          <dd>{course.creatorId}</dd>
        </div>
      </dl>
      {onClick && (
        <button className="cardAction" type="button" onClick={onClick}>
          상세 보기
        </button>
      )}
    </article>
  );
}

function formatPrice(price) {
  return new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0,
  }).format(price);
}
