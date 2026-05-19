import React, { useEffect, useState } from 'react';
import { cancelEnrollment, confirmPayment, getMyEnrollments } from './enrollmentApi.js';

export function MyEnrollments({ user }) {
  const [enrollments, setEnrollments] = useState([]);
  const [isLoading, setLoading] = useState(true);
  const [mutatingCourseId, setMutatingCourseId] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadEnrollments() {
      setLoading(true);
      setError('');

      try {
        const data = await getMyEnrollments(user.id);
        if (!ignore) {
          setEnrollments(data || []);
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

    loadEnrollments();
    return () => {
      ignore = true;
    };
  }, [user.id]);

  async function handleMutation(courseId, action) {
    setError('');
    setMutatingCourseId(courseId);

    try {
      const updated = action === 'confirm'
        ? await confirmPayment(user.id, courseId)
        : await cancelEnrollment(user.id, courseId);
      setEnrollments((items) => items.map((item) => (
        item.courseId === updated.courseId ? updated : item
      )));
    } catch (err) {
      setError(err.message);
    } finally {
      setMutatingCourseId(null);
    }
  }

  return (
    <section className="profilePanel">
      <div className="sectionHeader">
        <div>
          <p className="eyebrow">ENROLLMENTS</p>
          <h2>내 수강 신청 목록</h2>
        </div>
      </div>
      {isLoading && <p className="statusText">수강 신청 목록을 불러오는 중입니다.</p>}
      {error && <p className="errorBox">{error}</p>}
      {!isLoading && !error && enrollments.length === 0 && (
        <div className="emptyInline">아직 수강 신청 내역이 없습니다.</div>
      )}
      {!isLoading && enrollments.length > 0 && (
        <div className="enrollmentList">
          {enrollments.map((enrollment) => (
            <article className="enrollmentItem" key={enrollment.id}>
              <div>
                <strong>강의 #{enrollment.courseId}</strong>
                <span className={`statusPill ${enrollment.status.toLowerCase()}`}>{enrollment.status}</span>
              </div>
              <dl>
                <div>
                  <dt>신청 ID</dt>
                  <dd>{enrollment.id}</dd>
                </div>
                <div>
                  <dt>결제 확정일</dt>
                  <dd>{enrollment.confirmedDate || '-'}</dd>
                </div>
              </dl>
              <div className="itemActions">
                {enrollment.status === 'PENDING' && (
                  <button
                    className="primaryButton"
                    type="button"
                    disabled={mutatingCourseId === enrollment.courseId}
                    onClick={() => handleMutation(enrollment.courseId, 'confirm')}
                  >
                    결제 확정
                  </button>
                )}
                {enrollment.status !== 'CANCELLED' && (
                  <button
                    className="secondaryButton"
                    type="button"
                    disabled={mutatingCourseId === enrollment.courseId}
                    onClick={() => handleMutation(enrollment.courseId, 'cancel')}
                  >
                    신청 취소
                  </button>
                )}
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
