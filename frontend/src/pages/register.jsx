import React, { useState } from 'react';
import '../styles/register.css';

// 1. Algoritmo de verificación chileno (Módulo 11)
function validarRutChileno(rutCompleto) {
  const rutLimpio = rutCompleto.replace(/[^0-9kK]/g, '').toUpperCase();
  if (rutLimpio.length < 2) return false;

  const cuerpo = rutLimpio.slice(0, -1);
  const dv = rutLimpio.slice(-1);

  let suma = 0;
  let multiplo = 2;

  for (let i = cuerpo.length - 1; i >= 0; i--) {
    suma += multiplo * parseInt(cuerpo.charAt(i), 10);
    multiplo = multiplo < 7 ? multiplo + 1 : 2;
  }

  const dvEsperadoNum = 11 - (suma % 11);
  let dvEsperado = '';
  if (dvEsperadoNum === 11) dvEsperado = '0';
  else if (dvEsperadoNum === 10) dvEsperado = 'K';
  else dvEsperado = dvEsperadoNum.toString();

  return dv === dvEsperado;
}

// 2. Formateador dinámico: 12345678k -> 12.345.678-K
function formatearRut(rut) {
  const valorLimpio = rut.replace(/[^0-9kK]/g, '').toUpperCase();
  if (valorLimpio.length <= 1) return valorLimpio;

  const cuerpo = valorLimpio.slice(0, -1);
  const dv = valorLimpio.slice(-1);

  const cuerpoFormateado = cuerpo.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

  return `${cuerpoFormateado}-${dv}`;
}

export default function Register({ onRegisterSuccess, onNavigateToLogin, onBackToHome }) {
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    rut: '',
    email: '',
    direccion: '',
    password: ''
  });

  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === 'rut') {
      const rutFormateado = formatearRut(value);
      setFormData((prev) => ({ ...prev, rut: rutFormateado }));
      return;
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validarRutChileno(formData.rut)) {
      setError('El RUT ingresado no es válido. Revisa los dígitos.');
      return;
    }

    if (!formData.email.endsWith('@gmail.com')) {
      setError('El correo debe terminar estrictamente en @gmail.com');
      return;
    }

    if (formData.password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    setIsLoading(true);

    try {
      const response = await fetch('/api/v1/usuario', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nombre: formData.nombre.trim(),
          apellido: formData.apellido.trim(),
          rut: formData.rut.trim(),
          email: formData.email.trim(),
          direccion: formData.direccion.trim(),
          password: formData.password
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || 'Error al guardar el usuario en el servidor');
      }

      const usuarioRegistrado = await response.json();
      alert('¡Usuario registrado con éxito!');

      // Opcional: Persistencia local si la aplicación lo requiere
      try {
        const storedAccounts = JSON.parse(localStorage.getItem('pedidos360_accounts') || '[]');
        const updatedAccounts = storedAccounts.filter(
            (acc) => acc.email.toLowerCase() !== usuarioRegistrado.email.toLowerCase()
        );
        localStorage.setItem('pedidos360_accounts', JSON.stringify([...updatedAccounts, usuarioRegistrado]));
      } catch (err) {
        console.error('Error al guardar localmente:', err);
      }

      if (onRegisterSuccess) {
        onRegisterSuccess(usuarioRegistrado);
      }
    } catch (err) {
      console.error('Error en el registro:', err);
      setError(err.message || 'No se pudo conectar con el microservicio de usuarios.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="register-container">
      <div className="register-card">
        <button type="button" className="register-back-btn" onClick={onBackToHome}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="19" y1="12" x2="5" y2="12" />
            <polyline points="12 19 5 12 12 5" />
          </svg>
          <span>Volver a la tienda</span>
        </button>

        <div className="register-header">
          <h2>Crear Cuenta</h2>
          <p>Completa tus datos para registrarte en Pedidos360</p>
        </div>

        {error && <div className="register-error-alert">{error}</div>}

        <form className="register-form" onSubmit={handleSubmit}>
          {/* Nombre y Apellido */}
          <div className="register-row">
            <div className="register-field-group">
              <label htmlFor="reg-nombre">Nombre *</label>
              <div className="register-input-wrapper">
                <svg className="register-input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <input
                  id="reg-nombre"
                  type="text"
                  name="nombre"
                  placeholder="Juan"
                  value={formData.nombre}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="register-field-group">
              <label htmlFor="reg-apellido">Apellido *</label>
              <div className="register-input-wrapper">
                <svg className="register-input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <input
                  id="reg-apellido"
                  type="text"
                  name="apellido"
                  placeholder="Pérez"
                  value={formData.apellido}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
          </div>

          {/* RUT y Correo */}
          <div className="register-row">
            <div className="register-field-group">
              <label htmlFor="reg-rut">RUT *</label>
              <div className="register-input-wrapper">
                <svg className="register-input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <rect x="1" y="4" width="22" height="16" rx="2" ry="2" />
                  <line x1="1" y1="10" x2="23" y2="10" />
                </svg>
                <input
                  id="reg-rut"
                  type="text"
                  name="rut"
                  placeholder="12.345.678-9"
                  maxLength={12}
                  value={formData.rut}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="register-field-group">
              <label htmlFor="reg-email">Correo Electrónico (@gmail.com) *</label>
              <div className="register-input-wrapper">
                <svg className="register-input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                  <polyline points="22,6 12,13 2,6" />
                </svg>
                <input
                  id="reg-email"
                  type="email"
                  name="email"
                  placeholder="usuario@gmail.com"
                  value={formData.email}
                  onChange={handleChange}
                  pattern=".+@gmail\.com"
                  title="El correo debe terminar en @gmail.com"
                  required
                />
              </div>
            </div>
          </div>

          {/* Dirección */}
          <div className="register-field-group">
            <label htmlFor="reg-direccion">Dirección *</label>
            <div className="register-input-wrapper">
              <svg className="register-input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                <circle cx="12" cy="10" r="3" />
              </svg>
              <input
                id="reg-direccion"
                type="text"
                name="direccion"
                placeholder="Av. Providencia 1234, Depto 402"
                value={formData.direccion}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          {/* Contraseña */}
          <div className="register-field-group">
            <label htmlFor="reg-password">Contraseña *</label>
            <div className="register-input-wrapper">
              <svg className="register-input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
              <input
                id="reg-password"
                type={showPassword ? 'text' : 'password'}
                name="password"
                placeholder="Mínimo 6 caracteres"
                value={formData.password}
                onChange={handleChange}
                required
              />
              <button
                type="button"
                className="register-toggle-visibility"
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

          <button type="submit" className="register-submit-btn" disabled={isLoading}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="8.5" cy="7" r="4" />
              <line x1="20" y1="8" x2="20" y2="14" />
              <line x1="23" y1="11" x2="17" y2="11" />
            </svg>
            <span>{isLoading ? 'Registrando...' : 'Crear Cuenta'}</span>
          </button>
        </form>

        <div className="register-footer">
          <span>¿Ya tienes una cuenta?</span>
          <button type="button" onClick={onNavigateToLogin}>
            Inicia sesión aquí
          </button>
        </div>
      </div>
    </div>
  );
}