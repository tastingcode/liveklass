import React, { useMemo, useState } from 'react';
import { CourseDetailPage } from './course/CourseDetailPage.jsx';
import { CourseHomePage } from './course/CourseHomePage.jsx';
import { CourseRegisterPage } from './course/CourseRegisterPage.jsx';
import { getStoredUser, saveStoredUser } from './user/currentUserStorage.js';
import { MyPage } from './user/MyPage.jsx';
import { SignupPage } from './user/SignupPage.jsx';
import { usePathname } from './shared/router.js';

export function App() {
  const pathname = usePathname();
  const [currentUser, setCurrentUser] = useState(getStoredUser);

  const auth = useMemo(() => ({
    user: currentUser,
    setUser(user) {
      setCurrentUser(user);
      saveStoredUser(user);
    },
  }), [currentUser]);

  if (pathname === '/signup') {
    return <SignupPage auth={auth} />;
  }

  if (pathname === '/mypage') {
    return <MyPage auth={auth} />;
  }

  if (pathname === '/courses/new') {
    return <CourseRegisterPage auth={auth} />;
  }

  if (pathname.startsWith('/courses/')) {
    return <CourseDetailPage auth={auth} courseId={pathname.split('/')[2]} />;
  }

  return <CourseHomePage auth={auth} />;
}
