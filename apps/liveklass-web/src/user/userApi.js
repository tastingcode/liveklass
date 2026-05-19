import { request } from '../shared/api.js';

export function loginUser(loginId) {
  return request('/api/v1/users/login', {
    method: 'POST',
    body: JSON.stringify({ loginId }),
  });
}

export function joinUser(form) {
  return request('/api/v1/users', {
    method: 'POST',
    body: JSON.stringify(form),
  });
}
