import { request } from '../shared/api.js';

function userHeaders(userId) {
  return {
    'X-USER-ID': String(userId),
  };
}

export function enrollCourse(userId, courseId) {
  return request('/api/v1/enrollments', {
    method: 'POST',
    headers: userHeaders(userId),
    body: JSON.stringify({ courseId: Number(courseId) }),
  });
}

export function getMyEnrollments(userId) {
  return request('/api/v1/enrollments/me', {
    headers: userHeaders(userId),
  });
}

export function confirmPayment(userId, courseId) {
  return request(`/api/v1/enrollments/${courseId}/payment`, {
    method: 'PATCH',
    headers: userHeaders(userId),
  });
}

export function cancelEnrollment(userId, courseId) {
  return request(`/api/v1/enrollments/${courseId}/cancel`, {
    method: 'PATCH',
    headers: userHeaders(userId),
  });
}

export function getCourseStudents(userId, courseId) {
  return request(`/api/v1/enrollments/courses/${courseId}/students?page=0&size=20`, {
    headers: userHeaders(userId),
  });
}
