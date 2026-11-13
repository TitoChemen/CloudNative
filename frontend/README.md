### 1. Tecnologías y herramientas

El ecosistema de desarrollo de este proyecto Frontend utiliza las siguientes herramientas:

**React 19** - Librería principal para la construcción de la interfaz de usuario
**Vite 8** - Entorno de desarrollo rápido y empaquetador (Bundler) del proyecto
**JavaScript** (ES6+) - Lenguaje de programación del ecosistema
**Oxlint v1** - Herramienta de análisis estático (linter) para garantizar la calidad y rendimiento del código
**MSAL (Microsoft Authentication Library)** - Integración corporativa para autenticación con Azure AD

### 2. Instalación de NPM

npm create vite@latest
npm install react-router-dom
npm i lucide-react

### 3. intalación de MSAL en el Frontend

npm i @azure/msal-browser
npm i @azure/msal-react

### 3. estructura del Frontend

frontend/                     <-- Carpeta raíz de tu espacio de trabajo
├── .env                      # Variables de entorno para MSAL / API Gateway
├── .gitignore                # Archivos omitidos en Git
├── .oxlintrc.json            # Configuración de linter (Oxlint)
├── index.html                # Entrada HTML de Vite
├── package.json              # Configuración y dependencias del frontend
├── package-lock.json         # Registro exacto de versiones de npm
├── README.md                 # Documentación del proyecto (Ubicación perfecta)
├── vite.config.js            # Configuración del bundler Vite
└── src/                      # Código fuente principal
    ├── assets/               # Recursos estáticos (Logos, imágenes)
    ├── components/           # Bloques de construcción visuales
    │   ├── common/           # Componentes genéricos
    │   │   ├── Button.jsx
    │   │   └── ProductCard.jsx
    │   └── layout/           # Componentes de estructura fija
    │       ├── Footer.jsx
    │       ├── Layout.jsx    # Envuelve el Navbar, Footer y el contenido dinámico
    │       └── Navbar.jsx
    ├── config/               # Configuraciones del sistema
    │   └── auth-config.js    # Parámetros y credenciales de MSAL
    ├── context/              # Manejo de estados globales
    │   ├── AuthContext.jsx   # Estado de sesión de Microsoft
    │   └── CartContext.jsx   # Estado del carrito de productos
    ├── hooks/                # Lógica compartida de React
    │   └── useFetch.js       # Hook para llamadas a la API Gateway
    ├── pages/                # Vistas o pantallas completas de la app
    │   ├── Home.jsx          # Página principal
    │   ├── login.jsx         # Pantalla de inicio de sesión
    │   └── productos.jsx     # Catálogo de productos
    ├── routes/               # Sistema de navegación
    │   ├── AppRoutes.jsx     # Definición de rutas de la aplicación
    │   └── ProtectedRoute.jsx # Filtro de seguridad para bloquear rutas
    ├── services/             # Módulos de comunicación de datos
    │   ├── api.js            # Instancia cliente de peticiones HTTP
    │   └── authService.js    # Lógica auxiliar para MSAL
    ├── styles/               # Estilos complementarios de la app
    ├── App.css               # Estilos del componente raíz
    ├── App.jsx               # Componente central de la aplicación
    ├── index.css             # Estilos globales base
    └── main.jsx              # Inicializador de React y MsalProvider