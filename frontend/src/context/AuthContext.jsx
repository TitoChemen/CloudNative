import React, { createContext, useContext, useState, useEffect } from 'react';
import { msalInstance } from '../config/auth-config';
import { EventType } from '@azure/msal-browser';
import {
  loginWithAzurePopup,
  loginWithCredentials,
  logoutUser,
  isUserAdmin
} from '../services/authService';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem('pedidos360_user_session');
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });

  const [authError, setAuthError] = useState('');
  const [isAuthenticating, setIsAuthenticating] = useState(false);

  // Normaliza el usuario proveniente de Microsoft Entra ID
  const extractAzureUser = (account) => {
    if (!account) return null;
    const fullName = account.name || 'Usuario';
    const nameParts = fullName.split(' ');
    const email = (
        account.username ||
        account.idTokenClaims?.email ||
        account.idTokenClaims?.preferred_username ||
        ''
    ).toLowerCase();

    return {
      nombre: nameParts[0] || 'Administrador',
      apellido: nameParts.slice(1).join(' ') || '',
      rut: '11.111.111-1',
      email: email,
      direccion: 'Av. Concha y Toro',
      provider: 'azure'
    };
  };

  // 1. Sincronización inicial, listeners de MSAL y comunicación del popup
  useEffect(() => {
    const syncAccount = () => {
      const active = msalInstance.getActiveAccount() || msalInstance.getAllAccounts()[0];
      if (active) {
        msalInstance.setActiveAccount(active);
        const mapped = extractAzureUser(active);
        setUser((prev) => prev || mapped);
        localStorage.setItem('pedidos360_user_session', JSON.stringify(mapped));
      }
    };

    syncAccount();

    // Eventos directos de MSAL
    const callbackId = msalInstance.addEventCallback((event) => {
      // Extraemos el payload a una variable para ayudar al tipado del editor
      const payload = event.payload;

      if (
          (event.eventType === EventType.LOGIN_SUCCESS || event.eventType === EventType.ACQUIRE_TOKEN_SUCCESS) &&
          payload &&
          payload.account
      ) {
        msalInstance.setActiveAccount(payload.account);
        const mapped = extractAzureUser(payload.account);
        setUser(mapped);
        localStorage.setItem('pedidos360_user_session', JSON.stringify(mapped));
      }
    });

    // Mensaje recibido cuando el popup se autentica y se cierra
    const handlePopupMessage = (event) => {
      if (event.origin === window.location.origin && event.data?.type === 'MSAL_LOGIN_SUCCESS') {
        const active = msalInstance.getActiveAccount() || msalInstance.getAllAccounts()[0];
        if (active) {
          msalInstance.setActiveAccount(active);
          const mapped = extractAzureUser(active);
          setUser(mapped);
          localStorage.setItem('pedidos360_user_session', JSON.stringify(mapped));
          setIsAuthenticating(false);
        }
      }
    };

    window.addEventListener('message', handlePopupMessage);

    return () => {
      if (callbackId) {
        msalInstance.removeEventCallback(callbackId);
      }
      window.removeEventListener('message', handlePopupMessage);
    };
  }, []);

  // 2. Persistir sesión en localStorage
  useEffect(() => {
    if (user) {
      localStorage.setItem('pedidos360_user_session', JSON.stringify(user));
    } else {
      localStorage.removeItem('pedidos360_user_session');
    }
  }, [user]);

  const loginWithMicrosoft = async () => {
    // Protección 1: Bloquear ejecuciones si ya se está procesando un inicio de sesión
    if (isAuthenticating) {
      console.warn('Ya hay un proceso de autenticación en curso.');
      return;
    }

    try {
      setIsAuthenticating(true);
      setAuthError('');

      // Protección 2: Si MSAL ya tiene una cuenta válida activa, usarla sin abrir emergente
      const active = msalInstance.getActiveAccount() || msalInstance.getAllAccounts()[0];
      if (active) {
        msalInstance.setActiveAccount(active);
        const mapped = extractAzureUser(active);
        setUser(mapped);
        localStorage.setItem('pedidos360_user_session', JSON.stringify(mapped));
        return mapped;
      }

      const azureUser = await loginWithAzurePopup();
      setUser(azureUser);
      localStorage.setItem('pedidos360_user_session', JSON.stringify(azureUser));
      return azureUser;
    } catch (err) {
      // Protección 3: Captura específica de interaction_in_progress y limpia de estado pegado
      if (
          err.errorCode === 'interaction_in_progress' ||
          err.message?.includes('interaction_in_progress')
      ) {
        console.warn('MSAL detectó una interacción en progreso. Limpiando llaves atascadas...');

        // Elimina llaves de interacción bloqueantes de sessionStorage
        Object.keys(sessionStorage).forEach((key) => {
          if (key.includes('msal.interaction.status')) {
            sessionStorage.removeItem(key);
          }
        });

        setAuthError('Había un proceso pendiente. Por favor, haz clic en "Iniciar sesión con Microsoft" nuevamente.');
      } else {
        setAuthError(err.message || 'Error al autenticar con Microsoft');
      }
      throw err;
    } finally {
      setIsAuthenticating(false);
    }
  };

  const loginLocal = async (email, password) => {
    try {
      // 1. Obtenemos el objeto de la cuenta desde authService
      const userData = await loginWithCredentials(email, password);

      // 2. Guardamos el objeto completo (con nombre, apellido, email, rut, etc.)
      setUser(userData);
      localStorage.getItem("pedidos360_active_user", JSON.stringify(userData));

      return userData;
    } catch (error) {
      console.error("Error en loginLocal:", error);
      throw error;
    }
  };

  const logout = async () => {
    // 1. Limpiar estado y localStorage PRIMERO
    setUser(null);
    localStorage.removeItem('pedidos360_user_session');

    // 2. Redirigir a Microsoft para matar la sesión allá
    await logoutUser(user);
  };

  const updateUser = (updatedData) => {
    const merged = { ...user, ...updatedData };
    setUser(merged);
    localStorage.setItem('pedidos360_user_session', JSON.stringify(merged));

    const currentAccounts = JSON.parse(localStorage.getItem('pedidos360_accounts') || '[]');
    const updatedAccounts = currentAccounts.map((acc) =>
        acc.email.toLowerCase() === merged.email.toLowerCase() ? merged : acc
    );
    localStorage.setItem('pedidos360_accounts', JSON.stringify(updatedAccounts));
  };

  const isAdmin = isUserAdmin(user?.email);

  return (
      <AuthContext.Provider
          value={{
            user,
            isAdmin,
            isAuthenticating,
            authError,
            setAuthError,
            loginWithMicrosoft,
            loginLocal,
            logout,
            updateUser
          }}
      >
        {children}
      </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider');
  }
  return context;
}