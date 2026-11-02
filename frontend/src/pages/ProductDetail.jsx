import { useEffect, useState } from "react";
import api from "../services/api";
import { useCart, formatCLP } from "../context/CartContext";

export default function ProductDetail({ id, onBack }) {
    const [product, setProduct] = useState(null);
    const { addToCart, cartItems } = useCart();

    useEffect(() => {
        // Se eliminó el /api/v1 duplicado
        api.get(`/api/v1/productos/${id}`)
            .then(data => setProduct(data))
            .catch(err => console.error("Error cargando producto:", err));
    }, [id]);

    const handleAddToCart = () => {
        if (!product) return;

        const itemInCart = cartItems.find((item) => item.id === product.id);
        const inCartQty = itemInCart ? itemInCart.quantity : 0;
        const availableStock = product.stock ?? 10;

        if (inCartQty >= availableStock) {
            alert(`No puedes añadir más. El stock máximo disponible es de ${availableStock} unidades.`);
            return;
        }

        addToCart(product);
    };

    if (!product) return <div style={{ padding: '4rem', textAlign: 'center' }}>Cargando detalles...</div>;

    const currentStock = product.stock ?? 0;
    const isOutOfStock = currentStock <= 0;

    return (
        <div className="product-detail-container" style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
            <button
                onClick={onBack}
                style={{
                    marginBottom: '2rem',
                    cursor: 'pointer',
                    background: 'none',
                    border: 'none',
                    color: '#2563eb',
                    fontWeight: 'bold'
                }}
            >
                ← Volver al catálogo
            </button>

            <div style={{ display: 'flex', gap: '2rem', flexWrap: 'wrap' }}>
                <div style={{ flex: '1', minWidth: '300px', backgroundColor: '#fff', borderRadius: '12px', padding: '1rem' }}>
                    <img
                        src={product.imagenUrl || product.img}
                        alt={product.nombre || product.title}
                        style={{ width: '100%', height: 'auto', objectFit: 'contain' }}
                    />
                </div>

                <div style={{ flex: '1', minWidth: '300px' }}>
                    <h2 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>{product.nombre || product.title}</h2>
                    <p style={{ color: '#64748b', marginBottom: '1.5rem' }}>{product.especificaciones || product.specs}</p>
                    <p style={{ marginBottom: '1rem' }}>{product.descripcion}</p>

                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', marginBottom: '1.5rem', color: '#0f172a' }}>
                        {formatCLP(product.precio || product.currentPrice)}
                    </div>

                    <button
                        type="button"
                        disabled={isOutOfStock}
                        style={{
                            width: '100%',
                            opacity: isOutOfStock ? 0.5 : 1,
                            cursor: isOutOfStock ? 'not-allowed' : 'pointer',
                            backgroundColor: isOutOfStock ? '#64748b' : '#2563eb',
                            padding: '1rem',
                            borderRadius: '8px',
                            color: 'white',
                            border: 'none',
                            fontWeight: '600',
                            fontSize: '1rem'
                        }}
                        onClick={handleAddToCart}
                    >
                        {isOutOfStock ? 'Sin Stock' : 'Añadir al Carro'}
                    </button>
                </div>
            </div>
        </div>
    );
}