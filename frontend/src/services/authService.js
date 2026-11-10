import { msalInstance, loginRequest } from "../config/auth-config";

export const loginWithAzurePopup = async () => {
  try {
    await msalInstance.loginRedirect(loginRequest);
  } catch (error) {
    console.error("Error al redirigir a Azure:", error);
    throw error;
  }
};

export const loginWithCredentials = async (email, password) => {
  // Garantizar que 'emailStr' sea un string, incluso si enviaron un objeto { email, password }
  const emailStr = typeof email === "object" && email !== null ? email.email : email;
  const passStr = typeof email === "object" && email !== null ? email.password : password;

  const cleanEmail = typeof emailStr === "string" ? emailStr.trim().toLowerCase() : "";

  const accounts = JSON.parse(localStorage.getItem("pedidos360_accounts") || "[]");

  const found = accounts.find(
      (acc) =>
          typeof acc.email === "string" &&
          acc.email.toLowerCase() === cleanEmail &&
          acc.password === passStr
  );

  if (!found) throw new Error("Credenciales inválidas. Verifica tu correo y contraseña.");
  return found;
};

export const logoutUser = async (user) => {
  try {
    if (user?.provider === "azure") {
      const account = msalInstance.getActiveAccount();
      // Se limpia también en memoria activa
      msalInstance.setActiveAccount(null);
      await msalInstance.logoutRedirect({
        account: account,
        postLogoutRedirectUri: window.location.origin,
      });
    }
  } catch (error) {
    console.error("Error al cerrar sesión:", error);
  }
};

export const isUserAdmin = (email) => {
  if (!email) return false;

  // Si 'email' llega como objeto por error, se extrae la propiedad de correo
  const emailStr = typeof email === "object" ? email.email : email;
  if (typeof emailStr !== "string") return false;

  const account = msalInstance.getActiveAccount();
  if (account) {
    // Valida si el rol viene configurado desde el manifiesto de Azure
    const roles = account.idTokenClaims?.roles || [];
    if (roles.includes("Admin")) return true;
  }

  const emailLower = emailStr.toLowerCase();

  // Validaciones de respaldo por dominio o nombre
  return (
      emailLower.includes("admin") ||
      emailLower.endsWith("@duoc.cl") ||
      emailLower.endsWith("@duocuc.cl") ||
      emailLower.endsWith(".onmicrosoft.com")
  );
};