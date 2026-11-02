import React, { createContext, useContext, useState, useEffect } from 'react';

const CartContext = createContext();

// Función auxiliar para convertir "$1.459.990" a número entero
export function parsePrice(priceString) {
  if (priceString === undefined || priceString === null) return 0;
  if (typeof priceString === 'number') return priceString;

  // Convertimos a string por seguridad antes de hacer el replace
  const clean = String(priceString).replace(/[^0-9]/g, '');
  return parseInt(clean, 10) || 0;
}

// Función auxiliar para formatear número a moneda chilena ($ CLP)
export function formatCLP(amount) {
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
    maximumFractionDigits: 0
  }).format(amount);
}

export function CartProvider({ children }) {
  const [cartItems, setCartItems] = useState(() => {
    try {
      const saved = localStorage.getItem('pedidos360_cart');
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  });

  useEffect(() => {
    localStorage.setItem('pedidos360_cart', JSON.stringify(cartItems));
  }, [cartItems]);

  // Agregar producto al carro
  const addToCart = (product) => {
    setCartItems((prev) => {
      const exists = prev.find((item) => item.id === product.id);
      if (exists) {
        return prev.map((item) =>
            item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      }
      return [...prev, { ...product, quantity: 1 }];
    });
  };

  // Disminuir cantidad o quitar si llega a cero
  const decreaseQuantity = (productId) => {
    setCartItems((prev) =>
        prev
            .map((item) =>
                item.id === productId ? { ...item, quantity: item.quantity - 1 } : item
            )
            .filter((item) => item.quantity > 0)
    );
  };

  // Eliminar producto completamente
  const removeFromCart = (productId) => {
    setCartItems((prev) => prev.filter((item) => item.id !== productId));
  };

  // Vaciar carrito
  const clearCart = () => {
    setCartItems([]);
  };

  // Cálculos agregados
  const totalCount = cartItems.reduce((acc, item) => acc + item.quantity, 0);

  const subtotal = cartItems.reduce((acc, item) => {
    // Si viene del backend usará 'item.precio', si es un mock antiguo usará 'item.currentPrice'
    const priceToUse = item.precio !== undefined ? item.precio : item.currentPrice;
    return acc + parsePrice(priceToUse) * item.quantity;
  }, 0);

  return (
      <CartContext.Provider
          value={{
            cartItems,
            addToCart,
            decreaseQuantity,
            removeFromCart,
            clearCart,
            totalCount,
            subtotal
          }}
      >
        {children}
      </CartContext.Provider>
  );
}

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart debe utilizarse dentro de un CartProvider');
  }
  return context;
};