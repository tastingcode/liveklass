import React, { useState } from 'react';
import { navigate } from '../shared/router.js';
import { loginUser } from './userApi.js';

export function AuthPanel({ auth }) {
  const [loginId, setLoginId] = useState('');
  const [isSubmitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  async function handleLogin(event) {
    event.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      const user = await loginUser(loginId);
      auth.setUser(user);
      setLoginId('');
      navigate('/');
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (auth.user) {
    return (
      <div className="userArea">
        <button className="userBadge" type="button" onClick={() => navigate('/mypage')}>
          <strong>{auth.user.loginId}</strong>
          <span>{auth.user.userRole}</span>
        </button>
        <button className="ghostButton" type="button" onClick={() => auth.setUser(null)}>
          로그아웃
        </button>
      </div>
    );
  }

  return (
    <form className="loginBox" onSubmit={handleLogin}>
      <label className="srOnly" htmlFor="loginId">로그인 ID</label>
      <input
        id="loginId"
        value={loginId}
        maxLength={10}
        onChange={(event) => setLoginId(event.target.value)}
        placeholder="로그인 ID"
      />
      <button className="primaryButton" type="submit" disabled={isSubmitting}>
        로그인
      </button>
      <button className="secondaryButton" type="button" onClick={() => navigate('/signup')}>
        회원가입
      </button>
      {error && <p className="authError">{error}</p>}
    </form>
  );
}
