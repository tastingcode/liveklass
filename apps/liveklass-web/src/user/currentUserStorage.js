const CURRENT_USER_KEY = 'liveklass.currentUser';

export function getStoredUser() {
  try {
    const value = localStorage.getItem(CURRENT_USER_KEY);
    return value ? JSON.parse(value) : null;
  } catch {
    return null;
  }
}

export function saveStoredUser(user) {
  if (user) {
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(user));
    return;
  }

  localStorage.removeItem(CURRENT_USER_KEY);
}
