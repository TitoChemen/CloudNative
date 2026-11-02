import React, { useMemo } from 'react';
import { formatCLP } from '../context/CartContext';
import '../styles/Historial.css';

export default function Historial({ user, onNavigateToProducts, onSelectTrackingOrder }) {
  const userIdentifier = user?.rut || user?.email;

  // Filtra únicamente las compras que le pertenecen al usuario conectado
  const userOrders = useMemo(() => {
    if (!userIdentifier) return [];
    try {
      const allOrders = JSON.parse(localStorage.getItem('pedidos360_all_orders') || '[]');
      return allOrders.filter((ord) => ord.userIdentifier === userIdentifier);
    } catch {
      return [];
    }
  }, [userIdentifier]);

  const handleTrack = (order) => {
    // Guarda esta orden específica como la activa en seguimiento
    localStorage.setItem('pedidos360_active_order', JSON.stringify(order));
    if (onSelectTrackingOrder) {
      onSelectTrackingOrder();
    }
  };

  return (
      <section className="history-wrapper">
        <header className="history-header">
          <h2>Historial de Mis Compras</h2>
          <p>Listado de todos los pedidos asociados a tu cuenta ({user?.email || 'Usuario'}).</p>
        </header>

        {userOrders.length === 0 ? (
            <div className="history-empty">
              <svg width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z" />
                <line x1="3" y1="6" x2="21" y2="6" />
                <path d="M16 10a4 4 0 0 1-8 0" />
              </svg>
              <h3>Aún no tienes compras registradas</h3>
              <p>Cuando finalices una orden en el catálogo, aparecerá aquí con su estado y boleta.</p>
              <button type="button" className="btn-history-shop" onClick={onNavigateToProducts}>
                Ir a Comprar
              </button>
            </div>
        ) : (
            <div className="history-list">
              {userOrders.map((order) => (
                  <div key={order.orderId} className="history-card">
                    <div className="history-card-top">
                      <div>
                        <span className="order-number">Orden #{order.orderId}</span>
                        <span className="order-date">{order.fecha}</span>
                      </div>
                      <div className="history-badges">
                        <span className="badge-tracking-code">{order.trackingId}</span>
                        <span className={`badge-status ${order.estado}`}>
                    {order.estado === 'confirmado'
                        ? 'Confirmado'
                        : order.estado === 'preparando'
                            ? 'En Bodega'
                            : order.estado === 'en_camino'
                                ? 'En Camino'
                                : 'Entregado'}
                  </span>
                      </div>
                    </div>

                    <div className="history-items-detail">
                      {order.items.map((item) => {
                        // Soporte dual para propiedades del backend nuevo y mock antiguo
                        const title = item.nombre || item.title;
                        const imgSrc = item.imagenUrl || item.img;
                        const priceText = item.precio !== undefined ? formatCLP(item.precio) : item.currentPrice;

                        return (
                            <div key={item.id} className="history-item-row">
                              {imgSrc && <img src={imgSrc} alt={title} />}
                              <div className="history-item-text">
                                <strong>{title}</strong>
                                <span>{item.quantity} u. × {priceText}</span>
                              </div>
                            </div>
                        );
                      })}
                    </div>

                    <div className="history-card-bottom">
                      <div className="history-dest-info">
                        <span>Entrega en:</span>
                        <strong>{order.cliente.direccion}, {order.cliente.comuna}</strong>
                      </div>

                      <div className="history-actions-box">
                        <div className="history-total">
                          <span>Total Pagado:</span>
                          <strong>{formatCLP(order.total)}</strong>
                        </div>
                        <button
                            type="button"
                            className="btn-track-order"
                            onClick={() => handleTrack(order)}
                        >
                          Ver Seguimiento
                        </button>
                      </div>
                    </div>
                  </div>
              ))}
            </div>
        )}
      </section>
  );
}