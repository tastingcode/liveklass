import React, { useState } from 'react';
import { AppShell } from '../shared/AppShell.jsx';
import { navigate } from '../shared/router.js';
import { joinUser } from './userApi.js';

export function SignupPage({ auth }) {
  const [form, setForm] = useState({ loginId: '', userRole: 'STUDENT' });
  const [isSubmitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      const user = await joinUser(form);
      auth.setUser(user);
      navigate('/');
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <AppShell auth={auth}>
      <main className="formPage">
        <section className="formPanel">
          <div className="formHeader">
            <p className="eyebrow">JOIN</p>
            <h1>회원가입</h1>
          </div>
          <form className="signupForm" onSubmit={handleSubmit}>
            <label>
              <span>로그인 ID</span>
              <input
                value={form.loginId}
                maxLength={10}
                onChange={(event) => setForm({ ...form, loginId: event.target.value })}
                placeholder="영문/숫자 10자 이내"
              />
            </label>
            <fieldset>
              <legend>역할</legend>
              <div className="segmented">
                <label>
                  <input
                    type="radio"
                    name="userRole"
                    value="STUDENT"
                    checked={form.userRole === 'STUDENT'}
                    onChange={(event) => setForm({ ...form, userRole: event.target.value })}
                  />
                  <span>수강생</span>
                </label>
                <label>
                  <input
                    type="radio"
                    name="userRole"
                    value="CREATOR"
                    checked={form.userRole === 'CREATOR'}
                    onChange={(event) => setForm({ ...form, userRole: event.target.value })}
                  />
                  <span>강사</span>
                </label>
              </div>
            </fieldset>
            {error && <p className="errorBox">{error}</p>}
            <div className="formActions">
              <button className="primaryButton wide" type="submit" disabled={isSubmitting || !form.loginId.trim()}>
                가입하기
              </button>
              <button className="secondaryButton wide" type="button" onClick={() => navigate('/')}>
                홈으로
              </button>
            </div>
          </form>
        </section>
      </main>
    </AppShell>
  );
}
