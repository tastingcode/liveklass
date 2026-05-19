const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export async function request(path, options = {}) {
  const controller = new AbortController();
  const timeoutId = window.setTimeout(() => controller.abort(), 8000);
  const { headers, ...fetchOptions } = options;

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...fetchOptions,
    signal: controller.signal,
    headers: {
      'Content-Type': 'application/json',
      ...(headers || {}),
    },
  }).catch((error) => {
    if (error.name === 'AbortError') {
      throw new Error('백엔드 응답 시간이 초과되었습니다.');
    }
    throw new Error('백엔드 서버에 연결할 수 없습니다.');
  }).finally(() => {
    window.clearTimeout(timeoutId);
  });

  const body = await response.json().catch(() => null);

  if (!response.ok) {
    const message = body?.meta?.message
      || (response.status >= 500 ? '백엔드 서버에 연결할 수 없습니다.' : '요청을 처리하지 못했습니다.');
    throw new Error(message);
  }

  return body?.data;
}
