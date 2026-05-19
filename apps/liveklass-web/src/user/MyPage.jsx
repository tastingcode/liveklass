import React from 'react';
import { MyEnrollments } from '../enrollment/index.js';
import { AppShell } from '../shared/AppShell.jsx';

export function MyPage({ auth }) {
  if (!auth.user) {
    return (
      <AppShell auth={auth}>
        <main className="formPage">
          <section className="emptyState">
            <h1>로그인이 필요합니다.</h1>
            <p>우측 상단에서 로그인한 뒤 마이페이지를 확인할 수 있습니다.</p>
          </section>
        </main>
      </AppShell>
    );
  }

  return (
    <AppShell auth={auth}>
      <main className="formPage widePage profilePage">
        <section className="profilePanel">
          <p className="eyebrow">MY PAGE</p>
          <h1>{auth.user.loginId}</h1>
          <dl className="profileList">
            <div>
              <dt>사용자 ID</dt>
              <dd>{auth.user.id}</dd>
            </div>
            <div>
              <dt>로그인 ID</dt>
              <dd>{auth.user.loginId}</dd>
            </div>
            <div>
              <dt>역할</dt>
              <dd>{auth.user.userRole}</dd>
            </div>
          </dl>
        </section>
        {auth.user.userRole === 'STUDENT' && <MyEnrollments user={auth.user} />}
      </main>
    </AppShell>
  );
}
