import React, { useState, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import '../styles/Products.css';

// Formateador de moneda para convertir valores numéricos en formato de pesos chilenos
const formatCLP = (amount) => {
  if (amount === undefined || amount === null) return '';
  if (typeof amount === 'string') return amount;
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
    maximumFractionDigits: 0
  }).format(amount);
};

export default function Products({ onNavigateToLogin, onNavigateToDetail, products = [] }) {
  const { user } = useAuth();
  const { addToCart, cartItems } = useCart();
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [showAuthModal, setShowAuthModal] = useState(false);

  const filteredProducts = useMemo(() => {
    return products.filter((product) => {
      // Lectura del slug desde ProductoDTO (product.slugCategoria) o datos mock/locales
      const categorySlug =
          product.slugCategoria ||
          product.categoria?.slug ||
          product.categoria?.nombre?.toLowerCase() ||
          product.category ||
          '';

      const title = product.nombre || product.title || '';
      const specs = product.especificaciones || product.specs || '';

      const matchesCategory =
          selectedCategory === 'all' || categorySlug.toLowerCase() === selectedCategory.toLowerCase();
      const matchesSearch =
          title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          specs.toLowerCase().includes(searchTerm.toLowerCase());

      return matchesCategory && matchesSearch;
    });
  }, [products, selectedCategory, searchTerm]);

  const handleAddToCartClick = (product) => {
    if (!user) {
      setShowAuthModal(true);
      return;
    }

    const itemInCart = cartItems.find((item) => item.id === product.id);
    const inCartQty = itemInCart ? itemInCart.quantity : 0;
    const availableStock = product.stock ?? 10;

    if (inCartQty >= availableStock) {
      alert(`No puedes añadir más. El stock máximo disponible es de ${availableStock} unidades.`);
      return;
    }

    addToCart(product);
  };

  return (
      <section className="catalog-wrapper">
        <div className="catalog-header">
          <h2>Catálogo de Productos</h2>
          <p>Explora todo nuestro equipamiento y tecnología disponible con retiro o despacho inmediato.</p>
        </div>

        <div className="catalog-controls">
          <div className="catalog-search">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
            <input
                type="text"
                placeholder="Buscar por marca, procesador o modelo..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="catalog-filters">
            <button
                type="button"
                className={`filter-btn ${selectedCategory === 'all' ? 'active' : ''}`}
                onClick={() => setSelectedCategory('all')}
            >
              Todos
            </button>
            <button
                type="button"
                className={`filter-btn ${selectedCategory === 'notebooks' ? 'active' : ''}`}
                onClick={() => setSelectedCategory('notebooks')}
            >
              Notebooks
            </button>
            <button
                type="button"
                className={`filter-btn ${selectedCategory === 'componentes' ? 'active' : ''}`}
                onClick={() => setSelectedCategory('componentes')}
            >
              Componentes
            </button>
            <button
                type="button"
                className={`filter-btn ${selectedCategory === 'perifericos' ? 'active' : ''}`}
                onClick={() => setSelectedCategory('perifericos')}
            >
              Periféricos
            </button>
          </div>
        </div>

        {filteredProducts.length === 0 ? (
            <div className="catalog-empty">
              <p>No se encontraron productos que coincidan con tu búsqueda.</p>
            </div>
        ) : (
            <div className="catalog-grid">
              {filteredProducts.map((product) => {
                const currentStock = product.stock ?? 0;
                const isOutOfStock = currentStock <= 0;

                // Mapeo unificado entre Spring Boot DTO y formato estático
                const title = product.nombre || product.title;
                const specs = product.especificaciones || product.specs;
                const imgSrc = product.imagenUrl || product.img;
                const priceFormatted = formatCLP(product.precio) || product.currentPrice;
                const refPriceFormatted = product.precioReferencial
                    ? formatCLP(product.precioReferencial)
                    : product.refPrice;
                const discountText = product.descuento
                    ? `-${product.descuento}%`
                    : product.discount;

                return (
                    <div key={product.id} className="catalog-card">
                      {product.badge && <span className="card-badge">{product.badge}</span>}

                      <div className="card-img-box">
                        {imgSrc && <img src={imgSrc} alt={title} className="card-img" />}
                      </div>

                      <div className="card-content">
                        <h3 className="card-title">{title}</h3>
                        <p className="card-specs">{specs}</p>

                        <div style={{ margin: '0.4rem 0 0.8rem 0', display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.85rem' }}>
                    <span style={{
                      width: '8px',
                      height: '8px',
                      borderRadius: '50%',
                      backgroundColor: isOutOfStock ? '#ef4444' : currentStock <= 3 ? '#eab308' : '#22c55e'
                    }} />
                          <span style={{ color: isOutOfStock ? '#ef4444' : 'var(--text-muted)', fontWeight: '600' }}>
                      {isOutOfStock ? 'Agotado (0 disponibles)' : `Disponibles: ${currentStock} unid.`}
                    </span>
                        </div>

                        <div className="card-pricing">
                          <div className="price-box">
                            <span className="current-price">{priceFormatted}</span>
                            {refPriceFormatted && <span className="ref-price">Ref: {refPriceFormatted}</span>}
                          </div>
                          {discountText && <span className="discount-pill">{discountText}</span>}
                        </div>

                        {/* Contenedor flexible con ambos botones */}
                        <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem' }}>
                          <button
                              type="button"
                              onClick={() => onNavigateToDetail(product.id)}
                              style={{
                                flex: 1,
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                backgroundColor: '#f1f5f9',
                                color: '#0f172a',
                                border: 'none',
                                borderRadius: '8px',
                                fontWeight: '600',
                                fontSize: '0.9rem',
                                cursor: 'pointer'
                              }}
                          >
                            Ver Detalles
                          </button>

                          <button
                              type="button"
                              className="card-buy-btn"
                              disabled={isOutOfStock}
                              style={{
                                flex: 1,
                                opacity: isOutOfStock ? 0.5 : 1,
                                cursor: isOutOfStock ? 'not-allowed' : 'pointer',
                                backgroundColor: isOutOfStock ? '#64748b' : '#2563eb',
                                padding: '0.8rem',
                                borderRadius: '8px',
                                color: 'white',
                                border: 'none',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center',
                                gap: '0.5rem'
                              }}
                              onClick={() => handleAddToCartClick(product)}
                          >
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                              <circle cx="9" cy="21" r="1" />
                              <circle cx="20" cy="21" r="1" />
                              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" />
                            </svg>
                            <span>{isOutOfStock ? 'Agotado' : 'Añadir'}</span>
                          </button>
                        </div>
                      </div>
                    </div>
                );
              })}
            </div>
        )}

        {showAuthModal && (
            <div className="auth-required-overlay" onClick={() => setShowAuthModal(false)}>
              <div className="auth-required-card" onClick={(e) => e.stopPropagation()}>
                <div className="auth-required-icon">
                  <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                </div>
                <h3>Inicio de Sesión Requerido</h3>
                <p>Para agregar los productos al carro se debe tener un inicio de sesión previo.</p>
                <span className="auth-required-question">¿Quiere iniciar sesión?</span>

                <div className="auth-required-actions">
                  <button
                      type="button"
                      className="btn-auth-cancel"
                      onClick={() => setShowAuthModal(false)}
                  >
                    Cancelar
                  </button>
                  <button
                      type="button"
                      className="btn-auth-confirm"
                      onClick={() => {
                        setShowAuthModal(false);
                        if (onNavigateToLogin) onNavigateToLogin();
                      }}
                  >
                    Iniciar sesión
                  </button>
                </div>
              </div>
            </div>
        )}
      </section>
  );
}