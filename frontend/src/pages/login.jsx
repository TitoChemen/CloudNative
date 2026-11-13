import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import '../styles/login.css';

export default function Login({ onLoginSuccess, onNavigateToRegister, onBackToHome }) {
  const { user, loginWithMicrosoft, loginLocal, isAuthenticating } = useAuth();

  const [formData, setFormData] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  // Si MSAL ya procesó el login o restauró la sesión, redirige automáticamente
  useEffect(() => {
    if (user && onLoginSuccess) {
      onLoginSuccess();
    }
  }, [user, onLoginSuccess]);

  const handleAzureLogin = async () => {
    try {
      setError('');
      const loggedUser = await loginWithMicrosoft();
      if (loggedUser && onLoginSuccess) {
        onLoginSuccess();
      }
    } catch (err) {
      // Ignorar si el usuario simplemente cerró la ventana emergente
      if (!err.message?.includes('user_cancelled')) {
        setError('Error al autenticar con Microsoft: ' + (err.message || ''));
      }
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!formData.email.endsWith('@gmail.com')) {
      setError('El correo debe terminar en @gmail.com');
      return;
    }

    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      const accounts = JSON.parse(localStorage.getItem('pedidos360_accounts') || '[]');
      const foundUser = accounts.find(
        (acc) => acc.email.toLowerCase() === formData.email.trim().toLowerCase()
      );

      loginLocal(foundUser || formData.email.trim());
      if (onLoginSuccess) onLoginSuccess();
    }, 400);
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <button type="button" className="login-back-btn" onClick={onBackToHome}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="19" y1="12" x2="5" y2="12" />
            <polyline points="12 19 5 12 12 5" />
          </svg>
          <span>Volver a la tienda</span>
        </button>

        <div className="login-header">
          <h2>Iniciar Sesión</h2>
          <p>Ingresa a tu cuenta de Pedidos360</p>
        </div>

        {error && <div className="login-error-alert">{error}</div>}

        <button
          type="button"
          onClick={handleAzureLogin}
          disabled={isAuthenticating}
          style={{
            width: '100%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '0.75rem',
            padding: '0.8rem',
            borderRadius: '8px',
            border: '1px solid var(--border-color)',
            backgroundColor: 'var(--bg-secondary)',
            color: 'var(--text-main)',
            fontWeight: '700',
            fontSize: '0.92rem',
            cursor: 'pointer',
            marginBottom: '1.25rem'
          }}
        >
          <svg width="18" height="18" viewBox="0 0 21 21">
            <rect x="1" y="1" width="9" height="9" fill="#f25022"/>
            <rect x="11" y="1" width="9" height="9" fill="#7fba00"/>
            <rect x="1" y="11" width="9" height="9" fill="#00a4ef"/>
            <rect x="11" y="11" width="9" height="9" fill="#ffb900"/>
          </svg>
          <span>{isAuthenticating ? 'Conectando...' : 'Continuar con Microsoft'}</span>
        </button>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.25rem', color: 'var(--text-muted)', fontSize: '0.82rem' }}>
          <div style={{ flex: 1, height: '1px', backgroundColor: 'var(--border-color)' }}></div>
          <span>o con correo regular</span>
          <div style={{ flex: 1, height: '1px', backgroundColor: 'var(--border-color)' }}></div>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="login-field-group">
            <label htmlFor="login-email">Correo Electrónico *</label>
            <div className="login-input-wrapper">
              <svg className="login-input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                <polyline points="22,6 12,13 2,6" />
              </svg>
              <input
                id="login-email"
                type="email"
                name="email"
                placeholder="ejemplo@gmail.com"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="login-field-group">
            <label htmlFor="login-password">Contraseña *</label>
            <div className="login-input-wrapper">
              <svg className="login-input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
              <input
                id="login-password"
                type={showPassword ? 'text' : 'password'}
                name="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
                required
              />
              <button
                type="button"
                className="login-toggle-visibility"
                onClick={() => setShowPassword(!showPassword)}
                tabIndex="-1"
              >
                {showPassword ? (
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                    <line x1="1" y1="1" x2="23" y2="23" />
                  </svg>
                ) : (
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                )}
              </button>
            </div>
          </div>

          <button type="submit" className="login-submit-btn" disabled={isLoading}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" />
              <polyline points="10 17 15 12 10 7" />
              <line x1="15" y1="12" x2="3" y2="12" />
            </svg>
            <span>{isLoading ? 'Comprobando...' : 'Entrar'}</span>
          </button>
        </form>

        <div className="login-footer">
          <span>¿No tienes una cuenta?</span>
          <button type="button" onClick={onNavigateToRegister}>
            Regístrate aquí
          </button>
        </div>
      </div>
    </div>
  );
}