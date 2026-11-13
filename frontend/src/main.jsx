import React from 'react';
import ReactDOM from 'react-dom/client';

import { EventType } from '@azure/msal-browser';
import { msalInstance } from './config/auth-config';
import { AuthProvider } from './context/AuthContext';
import App from './App';
import './styles/index.css';

msalInstance.initialize().then(() => {
  return msalInstance.handleRedirectPromise();
}).then((authResult) => {
  if (authResult?.account) {
    msalInstance.setActiveAccount(authResult.account);
  } else {
    const accounts = msalInstance.getAllAccounts();
    if (accounts.length > 0) {
      msalInstance.setActiveAccount(accounts[0]);
    }
  }

  msalInstance.addEventCallback((event) => {
    if (event.eventType === EventType.LOGIN_SUCCESS && event.payload?.account) {
      msalInstance.setActiveAccount(event.payload.account);
    }
  });

  ReactDOM.createRoot(document.getElementById('root')).render(
      <React.StrictMode>
          <AuthProvider>
            <App />
          </AuthProvider>
      </React.StrictMode>
  );
}).catch((error) => {
  console.error("Error al inicializar MSAL:", error);
});