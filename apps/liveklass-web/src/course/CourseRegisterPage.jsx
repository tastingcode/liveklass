import React, { useState } from 'react';
import { AppShell } from '../shared/AppShell.jsx';
import { navigate } from '../shared/router.js';
import { registerCourse } from './courseApi.js';

const initialForm = {
  title: '',
  description: '',
  price: 0,
  capacity: 1,
  startDate: '',
  endDate: '',
};

export function CourseRegisterPage({ auth }) {
  const [form, setForm] = useState(initialForm);
  const [isSubmitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();

    if (!auth.user) {
      setError('로그인이 필요합니다.');
      return;
    }

    setError('');
    setSubmitting(true);

    try {
      const course = await registerCourse(auth.user.id, form);
      navigate(`/courses/${course.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (!auth.user || auth.user.userRole !== 'CREATOR') {
    return (
      <AppShell auth={auth}>
        <main className="formPage">
          <section className="emptyState">
            <h1>강사 로그인이 필요합니다.</h1>
            <p>강의 등록은 `CREATOR` 역할 사용자만 사용할 수 있습니다.</p>
          </section>
        </main>
      </AppShell>
    );
  }

  return (
    <AppShell auth={auth}>
      <main className="formPage widePage">
        <section className="formPanel">
          <div className="formHeader">
            <p className="eyebrow">COURSE</p>
            <h1>강의 등록</h1>
          </div>
          <form className="courseForm" onSubmit={handleSubmit}>
            <label className="fullField">
              <span>강의 제목</span>
              <input
                value={form.title}
                onChange={(event) => setForm({ ...form, title: event.target.value })}
                placeholder="예: 자바 기초"
              />
            </label>
            <label className="fullField">
              <span>강의 설명</span>
              <textarea
                value={form.description}
                onChange={(event) => setForm({ ...form, description: event.target.value })}
                placeholder="강의 내용을 입력해주세요."
              />
            </label>
            <label>
              <span>가격</span>
              <input
                type="number"
                min="0"
                value={form.price}
                onChange={(event) => setForm({ ...form, price: event.target.value })}
              />
            </label>
            <label>
              <span>정원</span>
              <input
                type="number"
                min="1"
                value={form.capacity}
                onChange={(event) => setForm({ ...form, capacity: event.target.value })}
              />
            </label>
            <label>
              <span>수강 시작일</span>
              <input
                type="date"
                value={form.startDate}
                onChange={(event) => setForm({ ...form, startDate: event.target.value })}
              />
            </label>
            <label>
              <span>수강 종료일</span>
              <input
                type="date"
                value={form.endDate}
                onChange={(event) => setForm({ ...form, endDate: event.target.value })}
              />
            </label>
            {error && <p className="errorBox fullField">{error}</p>}
            <div className="formActions fullField">
              <button className="primaryButton wide" type="submit" disabled={isSubmitting}>
                등록하기
              </button>
              <button className="secondaryButton wide" type="button" onClick={() => navigate('/')}>
                홈으로
              </button>
            </div>
          </form>
        </section>
      </main>
    </AppShell>
  );
}
