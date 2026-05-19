import React, { useState } from 'react';
import { enrollCourse } from './enrollmentApi.js';

export function EnrollmentAction({ auth, course, onEnrolled }) {
  const [isSubmitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const canEnroll = auth.user?.userRole === 'STUDENT' && course.status === 'OPEN' && auth.user.id !== course.creatorId;

  async function handleEnroll() {
    setError('');
    setMessage('');
    setSubmitting(true);

    try {
      const enrollment = await enrollCourse(auth.user.id, course.id);
      setMessage(`수강 신청이 완료되었습니다. 현재 상태: ${enrollment.status}`);
      onEnrolled?.(enrollment);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (!auth.user) {
    return (
      <section className="sidePanel">
        <h2>수강 신청</h2>
        <p>학생으로 로그인하면 수강 신청을 진행할 수 있습니다.</p>
      </section>
    );
  }

  if (!canEnroll) {
    return null;
  }

  return (
    <section className="sidePanel">
      <h2>수강 신청</h2>
      <p>이 강의는 현재 모집 중입니다.</p>
      <button className="primaryButton wide" type="button" disabled={isSubmitting} onClick={handleEnroll}>
        수강 신청
      </button>
      {message && <p className="successText">{message}</p>}
      {error && <p className="errorText">{error}</p>}
    </section>
  );
}
