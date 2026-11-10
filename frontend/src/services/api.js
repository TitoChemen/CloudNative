import { msalInstance, apiRequest } from "../config/auth-config";

// Si VITE_API_URL no está definido, usa string vacío para llamadas relativas con Nginx
const API_BASE_URL = import.meta.env.VITE_API_URL ?? "";

async function getAccessToken() {
  let account = msalInstance.getActiveAccount();
  if (!account) {
    const accounts = msalInstance.getAllAccounts();
    if (accounts.length > 0) {
      account = accounts[0];
      msalInstance.setActiveAccount(account);
    } else {
      // Retorna null si no hay usuario autenticado (permite endpoints públicos como productos)
      return null;
    }
  }

  try {
    const response = await msalInstance.acquireTokenSilent({
      ...apiRequest,
      account: account,
    });
    return response.accessToken;
  } catch (error) {
    try {
      const response = await msalInstance.acquireTokenPopup({
        ...apiRequest,
        account: account,
      });
      return response.accessToken;
    } catch (popupErr) {
      console.warn("No se pudo obtener token de acceso:", popupErr);
      return null;
    }
  }
}

export async function request(endpoint, options = {}) {
  const token = await getAccessToken();

  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  // Solo adjunta la cabecera Authorization si existe un token válido
  if (token) {
    console.log("Token enviado al backend:", token);
    headers["Authorization"] = `Bearer ${token}`;
  }

  const fullUrl = endpoint.startsWith("http") ? endpoint : `${API_BASE_URL}${endpoint}`;

  const response = await fetch(fullUrl, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    throw new Error("401 Unauthorized: El token fue rechazado por el backend.");
  }

  if (!response.ok) {
    throw new Error(`Error ${response.status}: ${response.statusText}`);
  }

  if (response.status === 204) return null;
  return response.json();
}

export const api = {
  get: (url, options) => request(url, { method: "GET", ...options }),
  post: (url, data, options) => request(url, { method: "POST", body: JSON.stringify(data), ...options }),
  put: (url, data, options) => request(url, { method: "PUT", body: JSON.stringify(data), ...options }),
  delete: (url, options) => request(url, { method: "DELETE", ...options }),
};

export default api;