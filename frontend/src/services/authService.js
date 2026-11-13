import { msalInstance, loginRequest } from "../config/auth-config";
import api from "./api";

export const loginWithAzurePopup = async () => {
  try {
    await msalInstance.loginRedirect(loginRequest);
  } catch (error) {
    console.error("Error al redirigir a Azure:", error);
    throw error;
  }
};

// Nuevo: Registrar usuario en usuario-service -> dispara RabbitMQ + Kafka (auditoria.actividad)
export const registerUser = async (usuarioData) => {
  return await api.post("/api/v1/usuarios", usuarioData);
};

// Actualizado: Valida credenciales contra usuario-service -> dispara auditoria.actividad en Kafka
export const loginWithCredentials = async (email, password) => {
  const emailStr = typeof email === "object" && email !== null ? email.email : email;
  const passStr = typeof email === "object" && email !== null ? email.password : password;
  const cleanEmail = typeof emailStr === "string" ? emailStr.trim().toLowerCase() : "";

  try {
    // Ajusta esta ruta si tu UsuarioController usa otro endpoint de login (ej. /api/v1/usuarios/login)
    const usuarioAutenticado = await api.post("/api/v1/usuarios/login", {
      email: cleanEmail,
      password: passStr,
    });
    return usuarioAutenticado;
  } catch (backendErr) {
    // Respaldo con localStorage si el endpoint de login aún no está desplegado
    const accounts = JSON.parse(localStorage.getItem("pedidos360_accounts") || "[]");
    const found = accounts.find(
      (acc) =>
        typeof acc.email === "string" &&
        acc.email.toLowerCase() === cleanEmail &&
        acc.password === passStr
    );
    if (!found) throw new Error("Credenciales inválidas. Verifica tu correo y contraseña.");
    return found;
  }
};

export const logoutUser = async (user) => {
  try {
    if (user?.provider === "azure") {
      const account = msalInstance.getActiveAccount();
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
  const emailStr = typeof email === "object" ? email.email : email;
  if (typeof emailStr !== "string") return false;

  const account = msalInstance.getActiveAccount();
  if (account) {
    const roles = account.idTokenClaims?.roles || [];
    if (roles.includes("Admin")) return true;
  }

  const emailLower = emailStr.toLowerCase();
  return (
    emailLower.includes("admin") ||
    emailLower.endsWith("@duoc.cl") ||
    emailLower.endsWith("@duocuc.cl") ||
    emailLower.endsWith(".onmicrosoft.com") ||
    emailLower.endsWith(".gmail.com")
  );
};