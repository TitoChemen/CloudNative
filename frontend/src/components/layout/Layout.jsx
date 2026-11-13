import React from 'react';
import Navbar from './Navbar';
import Footer from './Footer';
import '../../styles/Layout.css';

export default function Layout({
  children,
  onNavigate,
  user,
  onLogout,
  theme,
  onToggleTheme,
  onUpdateUser
}) {
  return (
    <div className="layout-wrapper">
      <Navbar
        onNavigate={onNavigate}
        user={user}
        onLogout={onLogout}
        theme={theme}
        onToggleTheme={onToggleTheme}
        onUpdateUser={onUpdateUser}
      />
      <main className="layout-content">
        {children}
      </main>
      <Footer onNavigate={onNavigate} />
    </div>
  );
}