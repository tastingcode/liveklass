import React, { useEffect, useState } from 'react';
import { getCourseStudents } from './enrollmentApi.js';

export function CourseStudents({ user, courseId }) {
  const [students, setStudents] = useState([]);
  const [pageInfo, setPageInfo] = useState(null);
  const [isLoading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadStudents() {
      setLoading(true);
      setError('');

      try {
        const page = await getCourseStudents(user.id, courseId);
        if (!ignore) {
          setStudents(page?.content || []);
          setPageInfo(page || null);
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

    loadStudents();
    return () => {
      ignore = true;
    };
  }, [courseId, user.id]);

  return (
    <section className="sidePanel">
      <h2>수강생 목록</h2>
      {isLoading && <p className="statusText compact">수강생 목록을 불러오는 중입니다.</p>}
      {error && <p className="errorBox compact">{error}</p>}
      {!isLoading && !error && students.length === 0 && (
        <p className="emptyInline">결제 확정된 수강생이 없습니다.</p>
      )}
      {!isLoading && students.length > 0 && (
        <div className="studentList">
          {students.map((student) => (
            <article className="studentItem" key={student.id}>
              <strong>학생 #{student.userId}</strong>
              <span className={`statusPill ${student.status.toLowerCase()}`}>{student.status}</span>
              <small>신청 #{student.id} · 강의 #{student.courseId}</small>
            </article>
          ))}
        </div>
      )}
      {pageInfo && (
        <p className="pageHint">
          총 {pageInfo.totalElements}명
        </p>
      )}
    </section>
  );
}
