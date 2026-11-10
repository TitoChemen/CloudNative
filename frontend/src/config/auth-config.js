import { PublicClientApplication } from "@azure/msal-browser";

export const msalConfig = {
  auth: {
    clientId: import.meta.env.VITE_AZURE_CLIENT_ID || "0a895358-1500-448a-982f-afc20822336c",
    authority: `https://login.microsoftonline.com/${import.meta.env.VITE_AZURE_TENANT_ID || "8680c901-cdee-41fb-b489-95b35c8ffaf0"}`,
    redirectUri: import.meta.env.VITE_REDIRECT_URI || window.location.origin,
  },
  cache: {
    cacheLocation: "localStorage",
    storeAuthStateInCookie: false,
  }
};

export const loginRequest = { scopes: ["User.Read"] };
export const apiRequest = { scopes: ["api://0a895358-1500-448a-982f-afc20822336c/access_as_user"] };

export const msalInstance = new PublicClientApplication(msalConfig);