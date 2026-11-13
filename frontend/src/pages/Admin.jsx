import React, { useState, useEffect } from 'react';
import api from '../services/api';
import '../styles/Admin.css';

// Limpia strings de precio tipo "$1.459.990" a números puros (1459990)
const parsePriceToNumber = (val) => {
  if (typeof val === 'number') return val;
  if (!val) return 0;
  return parseInt(val.replace(/[^0-9]/g, ''), 10) || 0;
};

// Formatea números a moneda chilena
const formatCurrency = (val) => {
  if (!val && val !== 0) return '$0';
  return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(val);
};

export default function Admin({ products, onUpdateProducts, onRefreshProducts }) {
  const [productList, setProductList] = useState(products || []);
  const [searchTerm, setSearchTerm] = useState('');
  const [editingProduct, setEditingProduct] = useState(null);
  const [isNewModalOpen, setIsNewModalOpen] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const [formData, setFormData] = useState({
    codigo: '',
    nombre: '',
    slugCategoria: 'notebooks',
    especificaciones: '',
    precio: '',
    precioReferencial: '',
    descuento: '',
    stock: 10,
    etiqueta: 'Disponible',
    imagenUrl: ''
  });

  const fetchInventory = async () => {
    try {
      setIsLoading(true);
      setErrorMsg('');
      const data = await api.get('/api/v1/productos');
      setProductList(data);
      if (onUpdateProducts) onUpdateProducts(data);
    } catch (err) {
      setErrorMsg('No se pudo cargar el inventario del servidor: ' + err.message);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchInventory();
  }, []);

  const criticalStockProducts = productList.filter((p) => (p.stock ?? 0) < 4);

  const filteredProducts = productList.filter((p) => {
    const title = p.nombre || p.title || '';
    const cat = p.slugCategoria || p.category || '';
    return title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        cat.toLowerCase().includes(searchTerm.toLowerCase());
  });

  const handleOpenCreate = () => {
    setEditingProduct(null);
    setFormData({
      codigo: `PROD-${Math.floor(1000 + Math.random() * 9000)}`,
      nombre: '',
      slugCategoria: 'notebooks',
      especificaciones: '',
      precio: '',
      precioReferencial: '',
      descuento: '',
      stock: 10,
      etiqueta: 'Disponible',
      imagenUrl: 'https://i.imgur.com/IwA5RR5.png'
    });
    setIsNewModalOpen(true);
  };

  const handleOpenEdit = (product) => {
    setEditingProduct(product);
    setFormData({
      codigo: product.codigo || '',
      nombre: product.nombre || product.title || '',
      slugCategoria: product.slugCategoria || product.category || 'notebooks',
      especificaciones: product.especificaciones || product.specs || '',
      precio: product.precio || parsePriceToNumber(product.currentPrice),
      precioReferencial: product.precioReferencial || parsePriceToNumber(product.refPrice),
      descuento: product.descuento || product.discount || '',
      stock: product.stock ?? 10,
      etiqueta: product.etiqueta || product.badge || 'Disponible',
      imagenUrl: product.imagenUrl || product.img || ''
    });
    setIsNewModalOpen(true);
  };

  const handleRestock = async (id) => {
    try {
      setErrorMsg('');
      const target = productList.find((p) => (p.idProducto || p.id) === id);
      if (!target) return;

      const newStock = (target.stock ?? 0) + 10;
      const payload = {
        ...target,
        stock: newStock,
        precio: target.precio || parsePriceToNumber(target.currentPrice),
        precioReferencial: target.precioReferencial || parsePriceToNumber(target.refPrice)
      };

      await api.put(`/api/v1/productos/${id}`, payload);
      await fetchInventory();
    } catch (err) {
      setErrorMsg('Error al reponer stock: ' + err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar este producto del inventario?')) return;
    try {
      setErrorMsg('');
      await api.delete(`/api/v1/productos/${id}`);
      await fetchInventory();
    } catch (err) {
      setErrorMsg('Error al eliminar producto: ' + err.message);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');

    const payload = {
      codigo: formData.codigo,
      nombre: formData.nombre,
      especificaciones: formData.especificaciones,
      precio: parsePriceToNumber(formData.precio),
      precioReferencial: parsePriceToNumber(formData.precioReferencial),
      stock: parseInt(formData.stock, 10),
      imagenUrl: formData.imagenUrl,
      etiqueta: formData.etiqueta,
      descuento: parseFloat(formData.descuento) || 0,
      slugCategoria: formData.slugCategoria,
      idCategoria: formData.slugCategoria === 'notebooks' ? 1 : formData.slugCategoria === 'perifericos' ? 2 : 3
    };

    try {
      if (editingProduct) {
        const prodId = editingProduct.idProducto || editingProduct.id;
        await api.put(`/api/v1/productos/${prodId}`, { ...payload, idProducto: prodId });
      } else {
        await api.post('/api/v1/productos', payload);
      }

      await fetchInventory();
      if (onRefreshProducts) onRefreshProducts();
      setIsNewModalOpen(false);
    } catch (err) {
      setErrorMsg('No se pudo guardar el producto: ' + err.message);
    }
  };

  return (
      <section className="admin-wrapper">
        <header className="admin-header">
          <div>
            <h2>Panel de Administración - Inventario</h2>
            <p>Control de existencias, precios y catálogo general de Pedidos360.</p>
          </div>
          <button type="button" className="btn-admin-add" onClick={handleOpenCreate}>
            + Agregar Producto
          </button>
        </header>

        {errorMsg && (
            <div style={{
              backgroundColor: 'rgba(239, 68, 68, 0.15)',
              color: '#ef4444',
              padding: '0.8rem',
              borderRadius: '8px',
              fontWeight: '600',
              marginBottom: '1rem',
              border: '1px solid #ef4444'
            }}>
              {errorMsg}
            </div>
        )}

        {/* Banner de alerta de stock crítico */}
        {criticalStockProducts.length > 0 && (
            <div className="admin-alert-banner">
              <div className="alert-banner-header">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2">
                  <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
                  <line x1="12" y1="9" x2="12" y2="13" />
                  <line x1="12" y1="17" x2="12.01" y2="17" />
                </svg>
                <div>
                  <strong>¡Alerta de Reposición Urgente!</strong>
                  <p>Hay {criticalStockProducts.length} producto(s) con stock en peligro (inferior a 4 unidades).</p>
                </div>
              </div>

              <div className="alert-critical-list">
                {criticalStockProducts.map((item) => {
                  const itemId = item.idProducto || item.id;
                  return (
                      <div key={itemId} className="alert-critical-item">
                  <span>
                    <strong>{item.nombre || item.title}</strong> — Quedan solo <b>{item.stock ?? 0} unid.</b>
                  </span>
                        <button
                            type="button"
                            className="btn-quick-restock"
                            onClick={() => handleRestock(itemId)}
                        >
                          + Reponer (+10)
                        </button>
                      </div>
                  );
                })}
              </div>
            </div>
        )}

        {/* Métricas rápidas */}
        <div className="admin-metrics-grid">
          <div className="metric-card">
            <span>Total Ítems</span>
            <strong>{productList.length}</strong>
          </div>
          <div className="metric-card">
            <span>Stock Total</span>
            <strong>{productList.reduce((acc, p) => acc + (p.stock ?? 0), 0)} u.</strong>
          </div>
          <div className={`metric-card ${criticalStockProducts.length > 0 ? 'metric-card-danger' : ''}`}>
            <span>En Peligro (&lt; 4 unid.)</span>
            <strong>{criticalStockProducts.length}</strong>
          </div>
        </div>

        {/* Buscador */}
        <div className="admin-search-bar">
          <input
              type="text"
              placeholder="Filtrar por nombre de producto o categoría..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Tabla de Productos */}
        <div className="admin-table-container">
          {isLoading ? (
              <div style={{ textAlign: 'center', padding: '2rem' }}>Cargando inventario...</div>
          ) : (
              <table className="admin-table">
                <thead>
                <tr>
                  <th>Código</th>
                  <th>Producto</th>
                  <th>Categoría</th>
                  <th>Precio Venta</th>
                  <th>Stock</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                {filteredProducts.map((product) => {
                  const itemId = product.idProducto || product.id;
                  const currentStock = product.stock ?? 0;
                  const isCritical = currentStock < 4;
                  const displayPrice = product.precio ? formatCurrency(product.precio) : product.currentPrice;
                  const displayImg = product.imagenUrl || product.img;
                  const displayTitle = product.nombre || product.title;
                  const displaySpecs = product.especificaciones || product.specs;
                  const displayCategory = product.slugCategoria || product.category;

                  return (
                      <tr key={itemId} className={isCritical ? 'tr-stock-critical' : ''}>
                        <td><strong>{product.codigo || `#${itemId}`}</strong></td>
                        <td>
                          <div className="admin-product-cell">
                            {displayImg && <img src={displayImg} alt="" />}
                            <div>
                              <strong>{displayTitle}</strong>
                              <span>{displaySpecs}</span>
                            </div>
                          </div>
                        </td>
                        <td><span className="badge-category">{displayCategory}</span></td>
                        <td><strong>{displayPrice}</strong></td>
                        <td>
                      <span className={`stock-indicator ${isCritical ? 'stock-danger' : ''}`}>
                        {currentStock} unid.
                      </span>
                          {isCritical && <span className="tag-reponer-urgente">¡Reponer!</span>}
                        </td>
                        <td>
                      <span className={`badge-badge ${isCritical ? 'badge-danger' : ''}`}>
                        {currentStock <= 0 ? 'Agotado' : isCritical ? 'Peligro' : (product.etiqueta || product.badge || 'Disponible')}
                      </span>
                        </td>
                        <td>
                          <div className="admin-actions-cell">
                            {isCritical && (
                                <button
                                    type="button"
                                    className="btn-action-restock"
                                    onClick={() => handleRestock(itemId)}
                                    title="Reponer 10 unidades automáticamente"
                                >
                                  +10
                                </button>
                            )}
                            <button
                                type="button"
                                className="btn-action-edit"
                                onClick={() => handleOpenEdit(product)}
                            >
                              Editar
                            </button>
                            <button
                                type="button"
                                className="btn-action-delete"
                                onClick={() => handleDelete(itemId)}
                            >
                              Eliminar
                            </button>
                          </div>
                        </td>
                      </tr>
                  );
                })}
                </tbody>
              </table>
          )}
        </div>

        {/* Modal Crear/Editar */}
        {isNewModalOpen && (
            <div className="admin-modal-overlay" onClick={() => setIsNewModalOpen(false)}>
              <div className="admin-modal-card" onClick={(e) => e.stopPropagation()}>
                <h3>{editingProduct ? 'Editar Producto' : 'Nuevo Producto en Inventario'}</h3>

                <form onSubmit={handleSubmit} className="admin-form">
                  <div className="form-row">
                    <div className="form-group">
                      <label>Código Producto *</label>
                      <input
                          type="text"
                          required
                          value={formData.codigo}
                          onChange={(e) => setFormData({ ...formData, codigo: e.target.value })}
                      />
                    </div>
                    <div className="form-group">
                      <label>Nombre del Producto *</label>
                      <input
                          type="text"
                          required
                          value={formData.nombre}
                          onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                      />
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="form-group">
                      <label>Categoría *</label>
                      <select
                          value={formData.slugCategoria}
                          onChange={(e) => setFormData({ ...formData, slugCategoria: e.target.value })}
                      >
                        <option value="notebooks">Notebooks</option>
                        <option value="componentes">Componentes</option>
                        <option value="perifericos">Periféricos</option>
                      </select>
                    </div>

                    <div className="form-group">
                      <label>Stock Disponibilidad *</label>
                      <input
                          type="number"
                          min="0"
                          required
                          value={formData.stock}
                          onChange={(e) => setFormData({ ...formData, stock: e.target.value })}
                      />
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="form-group">
                      <label>Precio Venta (CLP) *</label>
                      <input
                          type="text"
                          required
                          placeholder="1459990"
                          value={formData.precio}
                          onChange={(e) => setFormData({ ...formData, precio: e.target.value })}
                      />
                    </div>

                    <div className="form-group">
                      <label>Precio Referencial (CLP)</label>
                      <input
                          type="text"
                          placeholder="1899990"
                          value={formData.precioReferencial}
                          onChange={(e) => setFormData({ ...formData, precioReferencial: e.target.value })}
                      />
                    </div>
                  </div>

                  <div className="form-group">
                    <label>URL Imagen Directa (Imgur / HTTPS) *</label>
                    <input
                        type="text"
                        required
                        placeholder="https://i.imgur.com/IwA5RR5.png"
                        value={formData.imagenUrl}
                        onChange={(e) => setFormData({ ...formData, imagenUrl: e.target.value })}
                    />
                  </div>

                  <div className="form-group">
                    <label>Especificaciones Breves *</label>
                    <input
                        type="text"
                        required
                        placeholder="Core i7 RTX 4060 16GB SSD 1TB"
                        value={formData.especificaciones}
                        onChange={(e) => setFormData({ ...formData, especificaciones: e.target.value })}
                    />
                  </div>

                  <div className="admin-modal-buttons">
                    <button
                        type="button"
                        className="btn-modal-cancel"
                        onClick={() => setIsNewModalOpen(false)}
                    >
                      Cancelar
                    </button>
                    <button type="submit" className="btn-modal-save">
                      {editingProduct ? 'Guardar Cambios' : 'Crear Producto'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
        )}
      </section>
  );
}