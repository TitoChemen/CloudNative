import React, { useState } from 'react';
import { useCart, formatCLP } from '../context/CartContext';
import api from '../services/api';
import '../styles/Carrito.css';

const COMUNAS_RM = [
  'Santiago Centro',
  'Providencia',
  'Las Condes',
  'Ñuñoa',
  'Puente Alto',
  'Maipú',
  'La Florida',
  'San Miguel',
  'Macul',
  'La Reina'
];

export default function Carrito({ user, products = [], onUpdateProducts, onNavigateToProducts, onNavigateToTracking }) {
  const { cartItems, addToCart, decreaseQuantity, removeFromCart, clearCart, subtotal } = useCart();

  const [deliveryData, setDeliveryData] = useState({
    nombre: user?.nombre || 'Cliente',
    apellido: user?.apellido || '',
    rut: user?.rut || '',
    direccion: user?.direccion || 'Av. Concha y Toro',
    comuna: 'Santiago Centro'
  });

  const [shippingMethod, setShippingMethod] = useState('express');

  const shippingCost =
      shippingMethod === 'express'
          ? 4990
          : shippingMethod === 'estandar'
              ? 2990
              : 0;

  const total = subtotal + shippingCost;

  const handleDeliveryChange = (e) => {
    const { name, value } = e.target;
    setDeliveryData((prev) => ({ ...prev, [name]: value }));
  };

  const handleAddUnit = (item) => {
    const targetProduct = products.find((p) => p.id === item.id);
    const maxStock = targetProduct ? (targetProduct.stock ?? 10) : 10;

    if (item.quantity >= maxStock) {
      alert(`No hay más stock disponible. El límite es de ${maxStock} unidades.`);
      return;
    }
    addToCart(item);
  };

  const handleCheckout = async () => {
    if (!deliveryData.direccion.trim()) {
      alert('Por favor indica una dirección de entrega válida.');
      return;
    }

    const orderId = `ORD-${Math.floor(100000 + Math.random() * 900000)}`;

    // 1. Enviar transacción a pago-service a través de API Gateway para disparar el evento en RabbitMQ
    try {
      const pagoRes = await api.post('/api/pagos/procesar', {
        pedidoId: orderId,
        monto: total,
        tarjeta: 'APROBADA',
        email: deliveryData.email || user?.email || 'usuario@gmail.com'
      }); 

      if (pagoRes?.estado === 'RECHAZADO') {
        alert(`Pago rechazado por el servidor: ${pagoRes.mensaje}`);
        return;
      }
    } catch (error) {
      console.error('Error al comunicarse con pago-service:', error);
      alert(`Error al procesar el pago en el servidor: ${error.message}`);
      return;
    }

    // 2. Descuenta el stock comprado de cada producto
    const updatedProducts = products.map((prod) => {
      const boughtItem = cartItems.find((c) => c.id === prod.id);
      if (boughtItem) {
        const remainingStock = Math.max(0, (prod.stock ?? 10) - boughtItem.quantity);
        return { ...prod, stock: remainingStock };
      }
      return prod;
    });

    // Actualiza inventario global
    if (onUpdateProducts) {
      onUpdateProducts(updatedProducts);
    }

    // 3. Crea orden en el historial local y seguimiento
    const userIdentifier = user?.rut || user?.email || 'anonimo';
    const nuevaOrden = {
      orderId: orderId,
      trackingId: `CL-TRK-${Math.floor(10000 + Math.random() * 90000)}`,
      userIdentifier: userIdentifier,
      fecha: new Date().toLocaleDateString('es-CL', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }),
      estado: 'confirmado',
      cliente: { ...deliveryData },
      metodoEnvio: shippingMethod,
      costoEnvio: shippingCost,
      items: [...cartItems],
      total: total
    };

    const allOrders = JSON.parse(localStorage.getItem('pedidos360_all_orders') || '[]');
    localStorage.setItem('pedidos360_all_orders', JSON.stringify([nuevaOrden, ...allOrders]));
    localStorage.setItem('pedidos360_active_order', JSON.stringify(nuevaOrden));

    clearCart();
    alert(`¡Compra confirmada! Pago enviado a RabbitMQ. Guía: ${nuevaOrden.trackingId}`);

    if (onNavigateToTracking) {
      onNavigateToTracking();
    }
  };

  return (
      <section className="cart-wrapper">
        <header className="cart-header">
          <h2>Mi Carrito y Despacho</h2>
          <p>Confirma tus datos de entrega y método de envío antes de pagar.</p>
        </header>

        {cartItems.length === 0 ? (
            <div className="cart-empty-box">
              <div className="cart-empty-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                  <circle cx="9" cy="21" r="1" />
                  <circle cx="20" cy="21" r="1" />
                  <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" />
                </svg>
              </div>
              <h3>Tu carrito está vacío</h3>
              <p>Aún no has añadido ningún producto. Revisa nuestro catálogo.</p>
              <button type="button" className="cart-shop-btn" onClick={onNavigateToProducts}>
                Explorar Catálogo
              </button>
            </div>
        ) : (
            <div className="cart-layout">
              <div className="cart-items-container">
                <div className="cart-items-header">
                  <span>Productos ({cartItems.length})</span>
                  <button type="button" className="btn-clear-cart" onClick={clearCart}>
                    Vaciar Carro
                  </button>
                </div>

                <div className="cart-items-list">
                  {cartItems.map((item) => {
                    const prodRef = products.find((p) => p.id === item.id);
                    const maxStock = prodRef ? (prodRef.stock ?? 10) : 10;

                    const title = item.nombre || item.title;
                    const specs = item.especificaciones || item.specs;
                    const imgSrc = item.imagenUrl || item.img;
                    const priceText = item.precio !== undefined ? formatCLP(item.precio) : item.currentPrice;

                    return (
                        <div key={item.id} className="cart-item-card">
                          <div className="cart-item-img-box">
                            {imgSrc && <img src={imgSrc} alt={title} />}
                          </div>

                          <div className="cart-item-details">
                            <span className="cart-item-badge">{item.badge || 'Disponible'}</span>
                            <h4 className="cart-item-title">{title}</h4>
                            <p className="cart-item-specs">{specs}</p>
                            <strong className="cart-item-unit-price">{priceText} c/u</strong>
                            <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                        (Máx. disponible: {maxStock})
                      </span>
                          </div>

                          <div className="cart-item-actions">
                            <div className="quantity-counter">
                              <button type="button" onClick={() => decreaseQuantity(item.id)}>-</button>
                              <span className="quantity-display">{item.quantity}</span>
                              <button type="button" onClick={() => handleAddUnit(item)}>+</button>
                            </div>

                            <button
                                type="button"
                                className="btn-remove-item"
                                onClick={() => removeFromCart(item.id)}
                                title="Eliminar producto"
                            >
                              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <polyline points="3 6 5 6 21 6" />
                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                              </svg>
                            </button>
                          </div>
                        </div>
                    );
                  })}
                </div>

                {/* Formulario de Delivery */}
                <div className="cart-delivery-form-card" style={{ marginTop: '2rem', padding: '1.5rem', backgroundColor: 'var(--bg-card)', border: '1px solid var(--border-color)', borderRadius: '12px' }}>
                  <h3 style={{ margin: '0 0 1rem 0', fontSize: '1.15rem', color: 'var(--text-main)' }}>
                    Dirección de Entrega
                  </h3>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                    <div>
                      <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-main)', fontWeight: '600' }}>
                        Destinatario
                      </label>
                      <input
                          type="text"
                          name="nombre"
                          value={`${deliveryData.nombre} ${deliveryData.apellido}`}
                          disabled
                          style={{ width: '100%', padding: '0.65rem', borderRadius: '6px', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-secondary)', color: 'var(--text-muted)' }}
                      />
                    </div>

                    <div>
                      <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-main)', fontWeight: '600' }}>
                        Comuna (RM)
                      </label>
                      <select
                          name="comuna"
                          value={deliveryData.comuna}
                          onChange={handleDeliveryChange}
                          style={{ width: '100%', padding: '0.65rem', borderRadius: '6px', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)' }}
                      >
                        {COMUNAS_RM.map((comuna) => (
                            <option key={comuna} value={comuna}>{comuna}</option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div style={{ marginTop: '1rem' }}>
                    <label style={{ display: 'block', fontSize: '0.85rem', marginBottom: '0.3rem', color: 'var(--text-main)', fontWeight: '600' }}>
                      Calle, Número, Depto o Casa
                    </label>
                    <input
                        type="text"
                        name="direccion"
                        value={deliveryData.direccion}
                        onChange={handleDeliveryChange}
                        placeholder="Ej: Av. Concha y Toro 1234"
                        style={{ width: '100%', padding: '0.65rem', borderRadius: '6px', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)' }}
                        required
                    />
                  </div>
                </div>
              </div>

              <aside className="cart-summary-card">
                <h3>Resumen de la Orden</h3>

                <div className="summary-shipping-choice">
                  <label className="shipping-option">
                    <input
                        type="radio"
                        name="shipping"
                        value="express"
                        checked={shippingMethod === 'express'}
                        onChange={() => setShippingMethod('express')}
                    />
                    <div className="option-label">
                      <strong>Express 24 hrs RM</strong>
                      <span>Despacho prioritario ($4.990)</span>
                    </div>
                  </label>

                  <label className="shipping-option">
                    <input
                        type="radio"
                        name="shipping"
                        value="estandar"
                        checked={shippingMethod === 'estandar'}
                        onChange={() => setShippingMethod('estandar')}
                    />
                    <div className="option-label">
                      <strong>Estándar RM</strong>
                      <span>Entrega en 48-72 hrs ($2.990)</span>
                    </div>
                  </label>

                  <label className="shipping-option">
                    <input
                        type="radio"
                        name="shipping"
                        value="retiro"
                        checked={shippingMethod === 'retiro'}
                        onChange={() => setShippingMethod('retiro')}
                    />
                    <div className="option-label">
                      <strong>Retiro Inmediato</strong>
                      <span>Hub Providencia ($0)</span>
                    </div>
                  </label>
                </div>

                <div className="summary-breakdown">
                  <div className="summary-row">
                    <span>Subtotal</span>
                    <span>{formatCLP(subtotal)}</span>
                  </div>
                  <div className="summary-row">
                    <span>Despacho</span>
                    <span>{shippingCost === 0 ? 'Gratis' : formatCLP(shippingCost)}</span>
                  </div>
                  <div className="summary-divider"></div>
                  <div className="summary-row summary-total">
                    <strong>Total Final</strong>
                    <strong>{formatCLP(total)}</strong>
                  </div>
                </div>

                <button type="button" className="btn-checkout" onClick={handleCheckout}>
                  Confirmar y Pagar
                </button>

                <button type="button" className="btn-continue-shopping" onClick={onNavigateToProducts}>
                  Seguir Comprando
                </button>
              </aside>
            </div>
        )}
      </section>
  );
}