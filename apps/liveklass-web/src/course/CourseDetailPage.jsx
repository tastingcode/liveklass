import React, { useEffect, useState } from 'react';
import { CourseStudents, EnrollmentAction } from '../enrollment/index.js';
import { AppShell } from '../shared/AppShell.jsx';
import { navigate } from '../shared/router.js';
import { closeCourse, getCourse, openCourse } from './courseApi.js';

export function CourseDetailPage({ auth, courseId }) {
  const [course, setCourse] = useState(null);
  const [isLoading, setLoading] = useState(true);
  const [isMutating, setMutating] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadCourse() {
      setLoading(true);
      setError('');

      try {
        const data = await getCourse(courseId);
        if (!ignore) {
          setCourse(data);
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

    loadCourse();
    return () => {
      ignore = true;
    };
  }, [courseId]);

  const isOwner = Boolean(auth.user && course && auth.user.id === course.creatorId);

  async function handleStatusChange(nextStatus) {
    setError('');
    setMessage('');
    setMutating(true);

    try {
      const updatedCourse = nextStatus === 'OPEN'
        ? await openCourse(auth.user.id, course.id)
        : await closeCourse(auth.user.id, course.id);
      setCourse({ ...updatedCourse, applicants: course.applicants });
      setMessage(nextStatus === 'OPEN' ? '모집을 시작했습니다.' : '모집을 마감했습니다.');
    } catch (err) {
      setError(err.message);
    } finally {
      setMutating(false);
    }
  }

  return (
    <AppShell auth={auth}>
      <main className="main detailMain">
        <button className="ghostButton backButton" type="button" onClick={() => navigate('/')}>
          홈으로
        </button>

        {isLoading && <p className="statusText">강의 정보를 불러오는 중입니다.</p>}
        {error && <p className="errorBox">{error}</p>}
        {message && <p className="successBox">{message}</p>}
        {!isLoading && !course && !error && (
          <div className="emptyState">
            <h1>강의를 찾을 수 없습니다.</h1>
          </div>
        )}
        {course && (
          <div className="detailLayout">
            <article className="detailPanel">
              <div className="detailHeader">
                <div>
                  <p className="eyebrow">{course.status}</p>
                  <h1>{course.title}</h1>
                </div>
                {isOwner && (
                  <div className="detailActions">
                    {course.status === 'DRAFT' && (
                      <button className="primaryButton" type="button" disabled={isMutating} onClick={() => handleStatusChange('OPEN')}>
                        모집 시작
                      </button>
                    )}
                    {course.status === 'OPEN' && (
                      <button className="secondaryButton" type="button" disabled={isMutating} onClick={() => handleStatusChange('CLOSED')}>
                        모집 마감
                      </button>
                    )}
                  </div>
                )}
              </div>
              <p className="detailDescription">{course.description}</p>
              <dl className="detailMeta">
                <div>
                  <dt>가격</dt>
                  <dd>{formatPrice(course.price)}</dd>
                </div>
                <div>
                  <dt>정원</dt>
                  <dd>{course.capacity}명</dd>
                </div>
                <div>
                  <dt>신청 인원</dt>
                  <dd>{course.applicants ?? 0}명</dd>
                </div>
                <div>
                  <dt>강사 ID</dt>
                  <dd>{course.creatorId}</dd>
                </div>
                <div>
                  <dt>수강 기간</dt>
                  <dd>{course.startDate} ~ {course.endDate}</dd>
                </div>
              </dl>
            </article>
            <aside className="detailSide">
              <EnrollmentAction
                auth={auth}
                course={course}
                onEnrolled={() => setCourse({ ...course, applicants: (course.applicants ?? 0) + 1 })}
              />
              {isOwner && <CourseStudents user={auth.user} courseId={course.id} />}
            </aside>
          </div>
        )}
      </main>
    </AppShell>
  );
}

function formatPrice(price) {
  return new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0,
  }).format(price);
}
