import React from 'react';
import { navigate } from './router.js';
import { AuthPanel } from '../user/AuthPanel.jsx';

export function AppShell({ auth, children }) {
  return (
    <div className="app">
      <header className="topbar">
        <button className="brand" type="button" onClick={() => navigate('/')}>
          <span className="brandMark">L</span>
          <span>Liveklass</span>
        </button>
        <AuthPanel auth={auth} />
      </header>
      {children}
    </div>
  );
}
