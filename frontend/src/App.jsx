import React, { useState, useEffect } from 'react';
import { CartProvider } from './context/CartContext';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/layout/Layout';
import Home from './pages/Home';
import Products from './pages/products';
import About from './pages/about';
import Contact from './pages/contact';
import Carrito from './pages/Carrito';
import Login from './pages/login';
import Register from './pages/register';
import Seguimiento from './pages/seguimiento';
import Historial from './pages/Historial';
import Admin from './pages/Admin';
import ProductDetail from "./pages/ProductDetail.jsx";
import Simulador from './pages/Simulador';
import './styles/App.css';

const API_GATEWAY_URL = '/api/v1/productos';

function AppContent() {
  const [currentPage, setCurrentPage] = useState('home');
  const [selectedProductId, setSelectedProductId] = useState(null);
  const { user, isAdmin, logout, updateUser } = useAuth();

  const [products, setProducts] = useState([]);
  const [loadingProducts, setLoadingProducts] = useState(true);

  const fetchProducts = async () => {
    try {
      setLoadingProducts(true);
      const response = await fetch(API_GATEWAY_URL);
      if (!response.ok) {
        throw new Error('Error al obtener los productos desde el servidor');
      }
      const data = await response.json();
      setProducts(data);
    } catch (error) {
      console.error('Error cargando el catálogo:', error);
    } finally {
      setLoadingProducts(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const [theme, setTheme] = useState(() => {
    return localStorage.getItem('pedidos360_theme') || 'light';
  });

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('pedidos360_theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  const handleUpdateProducts = (updated) => {
    setProducts(updated);
  };

  const handleLogout = () => {
    logout();
    if (currentPage === 'admin' || currentPage === 'history' || currentPage === 'simulador') {
      setCurrentPage('home');
    }
  };

  return (
      <CartProvider>
        <Layout
            onNavigate={setCurrentPage}
            user={user}
            onLogout={handleLogout}
            theme={theme}
            onToggleTheme={toggleTheme}
            onUpdateUser={updateUser}
        >
          {currentPage === 'home' && <Home />}

          {currentPage === 'products' && (
              loadingProducts ? (
                  <div style={{ textAlign: 'center', padding: '4rem' }}>Cargando catálogo...</div>
              ) : (
                  <Products
                      user={user}
                      products={products}
                      onNavigateToLogin={() => setCurrentPage('login')}
                      onNavigateToDetail={(id) => {
                        setSelectedProductId(id);
                        setCurrentPage('productDetail');
                      }}
                  />
              )
          )}
          {currentPage === 'productDetail' && (
              <ProductDetail
                  id={selectedProductId}
                  onBack={() => setCurrentPage('products')}
              />
          )}

          {currentPage === 'about' && <About />}
          {currentPage === 'contact' && <Contact />}

          {currentPage === 'cart' && (
              <Carrito
                  user={user}
                  products={products}
                  onUpdateProducts={handleUpdateProducts}
                  onNavigateToProducts={() => setCurrentPage('products')}
                  onNavigateToTracking={() => setCurrentPage('tracking')}
              />
          )}

          {currentPage === 'tracking' && (
              <Seguimiento onNavigateToProducts={() => setCurrentPage('products')} />
          )}

          {currentPage === 'history' && (
              <Historial
                  user={user}
                  onNavigateToProducts={() => setCurrentPage('products')}
                  onSelectTrackingOrder={() => setCurrentPage('tracking')}
              />
          )}

          {/* VISTA ADMINISTRADOR */}
          {currentPage === 'admin' && (
              isAdmin ? (
                  <Admin
                      products={products}
                      onUpdateProducts={handleUpdateProducts}
                      onRefreshProducts={fetchProducts}
                  />
              ) : (
                  <div style={{ textAlign: 'center', padding: '5rem 1rem' }}>
                    <h2 style={{ color: '#ef4444' }}>Acceso Restringido</h2>
                    <p>Debes iniciar sesión con la cuenta de administrador para acceder a este panel.</p>
                    <button
                        type="button"
                        style={{
                          marginTop: '1rem',
                          padding: '0.65rem 1.25rem',
                          backgroundColor: '#2563eb',
                          color: '#fff',
                          border: 'none',
                          borderRadius: '6px',
                          cursor: 'pointer',
                          fontWeight: '700'
                        }}
                        onClick={() => setCurrentPage('login')}
                    >
                      Ir al Inicio de Sesión
                    </button>
                  </div>
              )
          )}

          {/* VISTA SIMULADOR RABBITMQ (SOLO ADMIN) */}
          {currentPage === 'simulador' && (
              isAdmin ? (
                  <Simulador />
              ) : (
                  <div style={{ textAlign: 'center', padding: '5rem 1rem' }}>
                    <h2 style={{ color: '#ef4444' }}>Acceso Restringido</h2>
                    <p>Debes iniciar sesión con la cuenta de administrador para acceder al simulador de eventos.</p>
                    <button
                        type="button"
                        style={{
                          marginTop: '1rem',
                          padding: '0.65rem 1.25rem',
                          backgroundColor: '#2563eb',
                          color: '#fff',
                          border: 'none',
                          borderRadius: '6px',
                          cursor: 'pointer',
                          fontWeight: '700'
                        }}
                        onClick={() => setCurrentPage('login')}
                    >
                      Ir al Inicio de Sesión
                    </button>
                  </div>
              )
          )}

          {currentPage === 'login' && (
              <Login
                  onLoginSuccess={() => setCurrentPage('home')}
                  onNavigateToRegister={() => setCurrentPage('register')}
                  onBackToHome={() => setCurrentPage('home')}
              />
          )}

          {currentPage === 'register' && (
              <Register
                  onRegisterSuccess={() => setCurrentPage('home')}
                  onNavigateToLogin={() => setCurrentPage('login')}
                  onBackToHome={() => setCurrentPage('home')}
              />
          )}
        </Layout>
      </CartProvider>
  );
}

export default function App() {
  return (
      <AuthProvider>
        <AppContent />
      </AuthProvider>
  );
}