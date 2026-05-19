import React from 'react';
import { createRoot } from 'react-dom/client';
import { App } from './App.jsx';
import './styles.css';

const rootElement = document.getElementById('root');
window.__liveklassRoot = window.__liveklassRoot || createRoot(rootElement);
window.__liveklassRoot.render(<App />);
