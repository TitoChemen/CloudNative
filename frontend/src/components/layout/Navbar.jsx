import React, { useState, useRef, useEffect } from 'react';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import '../../styles/Navbar.css';

// 1. Algoritmo de validación chileno (Módulo 11)
function validarRutChileno(rutCompleto) {
  if (!rutCompleto) return false;
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

// 2. Formateador dinámico
function formatearRut(rut) {
  const valorLimpio = rut.replace(/[^0-9kK]/g, '').toUpperCase().slice(0, 9);
  if (valorLimpio.length <= 1) return valorLimpio;

  const cuerpo = valorLimpio.slice(0, -1);
  const dv = valorLimpio.slice(-1);

  const cuerpoFormateado = cuerpo.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  return `${cuerpoFormateado}-${dv}`;
}

export default function Navbar({ onNavigate, theme, onToggleTheme }) {
  const { totalCount } = useCart();
  const { user, isAdmin, logout, updateUser } = useAuth();

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [rutError, setRutError] = useState('');
  const dropdownRef = useRef(null);

  const getProfile = () => {
    if (typeof user === 'object' && user !== null) {
      return {
        nombre: user.nombre || '',
        apellido: user.apellido || '',
        rut: user.rut ? formatearRut(user.rut) : '',
        email: user.email || '',
        direccion: user.direccion || ''
      };
    }
    return {
      nombre: '',
      apellido: '',
      rut: '',
      email: typeof user === 'string' ? user : '',
      direccion: ''
    };
  };

  const [editForm, setEditForm] = useState(getProfile);

  useEffect(() => {
    setEditForm(getProfile());
  }, [user]);

  const handleNavClick = (view) => {
    if (onNavigate) onNavigate(view);
  };

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleConfirmLogout = () => {
    setShowLogoutConfirm(false);
    setIsDropdownOpen(false);
    logout();
  };

  const handleOpenProfile = () => {
    setRutError('');
    setEditForm(getProfile());
    setIsDropdownOpen(false);
    setShowProfileModal(true);
  };

  const handleRutChange = (e) => {
    const rawValue = e.target.value;
    const formatted = formatearRut(rawValue);
    setEditForm((prev) => ({ ...prev, rut: formatted }));

    if (rutError) {
      setRutError('');
    }
  };

  const handleSaveProfile = (e) => {
    e.preventDefault();

    if (!validarRutChileno(editForm.rut)) {
      setRutError('El RUT ingresado no es válido. Revisa los dígitos.');
      return;
    }

    updateUser(editForm);
    setShowProfileModal(false);
  };

  const displayName = (() => {
    if (typeof user === 'object' && user !== null) {
      if (user.nombre) return user.nombre;
      if (user.name) return user.name;
      if (user.email && typeof user.email === 'string') return user.email.split('@')[0];
    }
    if (typeof user === 'string' && user.includes('@')) {
      return user.split('@')[0];
    }
    return typeof user === 'string' ? user : 'Usuario';
  })();

  return (
    <>
      <header className="navbar-wrapper">
        <nav className="navbar">
          <a
            href="#inicio"
            className="nav-brand"
            onClick={(e) => {
              e.preventDefault();
              handleNavClick('home');
            }}
          >
            Pedidos<span>360</span>
          </a>

          {/* Enlaces Principales */}
          <ul className="nav-links">
            <li>
              <a
                href="#inicio"
                onClick={(e) => {
                  e.preventDefault();
                  handleNavClick('home');
                }}
              >
                Inicio
              </a>
            </li>
            <li>
              <a
                href="#productos"
                onClick={(e) => {
                  e.preventDefault();
                  handleNavClick('products');
                }}
              >
                Productos
              </a>
            </li>
            <li>
              <a
                href="#nosotros"
                onClick={(e) => {
                  e.preventDefault();
                  handleNavClick('about');
                }}
              >
                Nosotros
              </a>
            </li>
            <li>
              <a
                href="#contactos"
                onClick={(e) => {
                  e.preventDefault();
                  handleNavClick('contact');
                }}
              >
                Contactos
              </a>
            </li>

            {/* Solo visible si es la cuenta de Azure de administración */}
            {isAdmin && (
              <>
                <li>
                  <a
                    href="#admin"
                    style={{ color: '#2563eb', fontWeight: '800' }}
                    onClick={(e) => {
                      e.preventDefault();
                      handleNavClick('admin');
                    }}
                  >
                    Admin
                  </a>
                </li>
                <li>
                  <a
                    href="#simulador"
                    style={{ color: '#8b5cf6', fontWeight: '800' }}
                    onClick={(e) => {
                      e.preventDefault();
                      handleNavClick('simulador');
                    }}
                  >
                    Simulador EDA
                  </a>
                </li>
              </>
            )}
          </ul>

          <div className="nav-actions">
            <button
              type="button"
              className="btn-theme-toggle"
              onClick={onToggleTheme}
              aria-label={theme === 'dark' ? 'Activar modo claro' : 'Activar modo oscuro'}
              title={theme === 'dark' ? 'Modo claro' : 'Modo oscuro'}
            >
              {theme === 'dark' ? (
                <svg className="icon-theme" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="5" />
                  <line x1="12" y1="1" x2="12" y2="3" />
                  <line x1="12" y1="21" x2="12" y2="23" />
                  <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
                  <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
                  <line x1="1" y1="12" x2="3" y2="12" />
                  <line x1="21" y1="12" x2="23" y2="12" />
                  <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
                  <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
                </svg>
              ) : (
                <svg className="icon-theme" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
                </svg>
              )}
            </button>

            {user ? (
              <div className="user-cart-container">
                <div className="account-dropdown-wrapper" ref={dropdownRef}>
                  <button
                    type="button"
                    className="btn-account-profile"
                    onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  >
                    <svg className="profile-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                      <circle cx="12" cy="7" r="4" />
                    </svg>
                    <div className="profile-text">
                      <span className="greeting-text">Hola, {displayName}</span>
                      <strong className="my-account-text">Mi cuenta</strong>
                    </div>
                  </button>

                  {isDropdownOpen && (
                    <div className="account-dropdown-menu">
                      <button
                        type="button"
                        className="dropdown-item"
                        onClick={handleOpenProfile}
                      >
                        Mis datos
                      </button>

                      <button
                        type="button"
                        className="dropdown-item"
                        onClick={() => {
                          handleNavClick('history');
                          setIsDropdownOpen(false);
                        }}
                      >
                        Mis compras
                      </button>

                      <button
                        type="button"
                        className="dropdown-item"
                        onClick={() => {
                          handleNavClick('tracking');
                          setIsDropdownOpen(false);
                        }}
                      >
                        Seguimiento de envío
                      </button>

                      {/* Solo visible en el dropdown si es la cuenta de Azure */}
                      {isAdmin && (
                        <>
                          <button
                            type="button"
                            className="dropdown-item"
                            style={{ color: '#2563eb', fontWeight: 'bold' }}
                            onClick={() => {
                              handleNavClick('admin');
                              setIsDropdownOpen(false);
                            }}
                          >
                            Panel Admin
                          </button>
                          <button
                            type="button"
                            className="dropdown-item"
                            style={{ color: '#8b5cf6', fontWeight: 'bold' }}
                            onClick={() => {
                              handleNavClick('simulador');
                              setIsDropdownOpen(false);
                            }}
                          >
                            Simulador EDA
                          </button>
                        </>
                      )}

                      <button
                        type="button"
                        className="dropdown-item"
                        onClick={() => {
                          handleNavClick('cart');
                          setIsDropdownOpen(false);
                        }}
                      >
                        Ver Carrito ({totalCount})
                      </button>

                      <div className="dropdown-divider"></div>

                      <button
                        type="button"
                        className="dropdown-item logout-item"
                        onClick={() => {
                          setIsDropdownOpen(false);
                          setShowLogoutConfirm(true);
                        }}
                      >
                        Cerrar sesión
                      </button>
                    </div>
                  )}
                </div>

                <span className="nav-vertical-divider" aria-hidden="true" />

                <button
                  type="button"
                  className="btn-cart-nav"
                  onClick={() => handleNavClick('cart')}
                  aria-label="Ver carrito"
                  title="Ver carrito"
                >
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ffffff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <circle cx="9" cy="21" r="1" />
                    <circle cx="20" cy="21" r="1" />
                    <circle cx="12" cy="7" r="4" />
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" />
                  </svg>
                  {totalCount > 0 && <span className="cart-badge-pill">{totalCount}</span>}
                </button>
              </div>
            ) : (
              <button
                type="button"
                className="btn-login"
                onClick={() => handleNavClick('login')}
              >
                <svg className="icon" viewBox="0 0 24 24">
                  <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <span>Iniciar sesión</span>
              </button>
            )}
          </div>
        </nav>
      </header>

      {/* Modal Profile y Logout permanecen iguales... */}
      {showProfileModal && (
        <div className="profile-modal-overlay" onClick={() => setShowProfileModal(false)}>
          <div className="profile-modal-card" onClick={(e) => e.stopPropagation()}>
            <div className="profile-modal-header">
              <div className="profile-modal-icon">
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </div>
              <div>
                <h3>Datos Generales del Usuario</h3>
                <p>Visualiza y edita tu información personal para envíos y facturación.</p>
              </div>
            </div>

            {rutError && (
              <div style={{
                backgroundColor: 'rgba(239, 68, 68, 0.15)',
                color: '#ef4444',
                padding: '0.6rem 0.8rem',
                borderRadius: '8px',
                fontSize: '0.85rem',
                fontWeight: '600',
                marginBottom: '1rem',
                textAlign: 'left'
              }}>
                {rutError}
              </div>
            )}

            <form onSubmit={handleSaveProfile} className="profile-edit-form">
              <div className="profile-form-grid">
                <div className="profile-form-field">
                  <label htmlFor="usr-name">Nombre</label>
                  <input
                    id="usr-name"
                    type="text"
                    value={editForm.nombre}
                    onChange={(e) => setEditForm({ ...editForm, nombre: e.target.value })}
                    required
                  />
                </div>

                <div className="profile-form-field">
                  <label htmlFor="usr-lastname">Apellido</label>
                  <input
                    id="usr-lastname"
                    type="text"
                    value={editForm.apellido}
                    onChange={(e) => setEditForm({ ...editForm, apellido: e.target.value })}
                    required
                  />
                </div>

                <div className="profile-form-field">
                  <label htmlFor="usr-rut">RUT</label>
                  <input
                    id="usr-rut"
                    type="text"
                    maxLength={12}
                    placeholder="12.345.678-9"
                    value={editForm.rut}
                    onChange={handleRutChange}
                    required
                  />
                </div>

                <div className="profile-form-field">
                  <label htmlFor="usr-email">Correo Electrónico</label>
                  <input
                    id="usr-email"
                    type="email"
                    value={editForm.email}
                    onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="profile-form-field">
                <label htmlFor="usr-address">Dirección</label>
                <input
                  id="usr-address"
                  type="text"
                  value={editForm.direccion}
                  onChange={(e) => setEditForm({ ...editForm, direccion: e.target.value })}
                  required
                />
              </div>

              <div className="profile-modal-actions">
                <button
                  type="button"
                  className="btn-profile-cancel"
                  onClick={() => setShowProfileModal(false)}
                >
                  Cancelar
                </button>
                <button type="submit" className="btn-profile-save">
                  Guardar Cambios
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showLogoutConfirm && (
        <div className="confirm-modal-overlay">
          <div className="confirm-modal-card">
            <h3>¿Estás seguro que quieres salir?</h3>
            <div className="confirm-modal-actions">
              <button
                type="button"
                className="btn-confirm-no"
                onClick={() => setShowLogoutConfirm(false)}
              >
                No
              </button>
              <button
                type="button"
                className="btn-confirm-yes"
                onClick={handleConfirmLogout}
              >
                Sí
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}