import React, { useEffect, useState } from 'react';
import { AppShell } from '../shared/AppShell.jsx';
import { navigate } from '../shared/router.js';
import { CourseCard } from './CourseCard.jsx';
import { getCourses } from './courseApi.js';

const courseStatuses = [
  { value: 'OPEN', label: '모집 중' },
  { value: 'DRAFT', label: '준비 중' },
  { value: 'CLOSED', label: '마감' },
];

export function CourseHomePage({ auth }) {
  const [selectedStatus, setSelectedStatus] = useState('OPEN');
  const [courses, setCourses] = useState([]);
  const [isLoading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadCourses() {
      setLoading(true);
      setError('');

      try {
        const page = await getCourses(selectedStatus);
        if (!ignore) {
          setCourses(page?.content || []);
        }
      } catch (err) {
        if (!ignore) {
          setError(err.message);
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    }

    loadCourses();
    return () => {
      ignore = true;
    };
  }, [selectedStatus]);

  const selectedStatusLabel = courseStatuses.find((status) => status.value === selectedStatus)?.label || selectedStatus;

  return (
    <AppShell auth={auth}>
      <main className="main">
        <section className="homeIntro">
          <div>
            <p className="eyebrow">OPEN COURSES</p>
            <h1>개설된 강의</h1>
          </div>
          {auth.user?.userRole === 'CREATOR' && (
            <button className="primaryButton" type="button" onClick={() => navigate('/courses/new')}>
              강의 등록
            </button>
          )}
        </section>

        <section className="filterBar" aria-label="강의 상태 필터">
          {courseStatuses.map((status) => (
            <button
              className={`filterButton${selectedStatus === status.value ? ' active' : ''}`}
              key={status.value}
              type="button"
              onClick={() => setSelectedStatus(status.value)}
            >
              {status.label}
            </button>
          ))}
        </section>

        {isLoading && <p className="statusText">강의 목록을 불러오는 중입니다.</p>}
        {error && <p className="errorBox">{error}</p>}
        {!isLoading && !error && courses.length === 0 && (
          <div className="emptyState">
            <h2>{selectedStatusLabel} 강의가 없습니다.</h2>
            <p>상단 필터를 바꾸면 다른 상태의 강의를 확인할 수 있습니다.</p>
          </div>
        )}
        {!isLoading && !error && courses.length > 0 && (
          <div className="courseGrid">
            {courses.map((course) => (
              <CourseCard key={course.id} course={course} onClick={() => navigate(`/courses/${course.id}`)} />
            ))}
          </div>
        )}
      </main>
    </AppShell>
  );
}
