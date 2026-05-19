import { request } from '../shared/api.js';

export function getCourses(status = 'OPEN') {
  return request(`/api/v1/courses?status=${status}&page=0&size=20`);
}

export function getCourse(courseId) {
  return request(`/api/v1/courses/${courseId}`);
}

export function registerCourse(userId, form) {
  return request('/api/v1/courses', {
    method: 'POST',
    headers: {
      'X-USER-ID': String(userId),
    },
    body: JSON.stringify({
      ...form,
      price: Number(form.price),
      capacity: Number(form.capacity),
    }),
  });
}

export function openCourse(userId, courseId) {
  return request(`/api/v1/courses/${courseId}/open`, {
    method: 'PATCH',
    headers: {
      'X-USER-ID': String(userId),
    },
  });
}

export function closeCourse(userId, courseId) {
  return request(`/api/v1/courses/${courseId}/close`, {
    method: 'PATCH',
    headers: {
      'X-USER-ID': String(userId),
    },
  });
}
