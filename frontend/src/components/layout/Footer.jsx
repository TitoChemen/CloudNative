import React from 'react';
import '../../styles/Footer.css';

export default function Footer({ onNavigate }) {
  const currentYear = new Date().getFullYear();

  const handleLinkClick = (view) => {
    if (onNavigate) {
      onNavigate(view);
    }
  };

  return (
    <footer className="footer">
      <div className="footer-container">
        {/* Columna Marca & Redes */}
        <div className="footer-col">
          <a
            href="#inicio"
            className="footer-brand"
            onClick={(e) => {
              e.preventDefault();
              handleLinkClick('home');
            }}
          >
            Pedidos<span>360</span>
          </a>
          <p className="footer-desc">
            Solución integral para la gestión y seguimiento de pedidos en tiempo real. Optimizamos la logística de tu negocio.
          </p>
          <div className="footer-socials">
            <a href="#web" aria-label="Sitio Web">
              <svg className="footer-icon-social" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10" />
                <line x1="2" y1="12" x2="22" y2="12" />
                <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10z" />
              </svg>
            </a>
            <a href="#compartir" aria-label="Redes">
              <svg className="footer-icon-social" viewBox="0 0 24 24">
                <circle cx="18" cy="5" r="3" />
                <circle cx="6" cy="12" r="3" />
                <circle cx="18" cy="19" r="3" />
                <line x1="8.59" y1="13.51" x2="15.42" y2="17.49" />
                <line x1="15.41" y1="6.51" x2="8.59" y2="10.49" />
              </svg>
            </a>
            <a href="#contacto" aria-label="Mensaje">
              <svg className="footer-icon-social" viewBox="0 0 24 24">
                <line x1="22" y1="2" x2="11" y2="13" />
                <polygon points="22 2 15 22 11 13 2 9 22 2" />
              </svg>
            </a>
          </div>
        </div>

        {/* Columna Navegación Directa */}
        <div className="footer-col">
          <h4 className="footer-title">Navegación</h4>
          <ul className="footer-links">
            <li>
              <a
                href="#inicio"
                onClick={(e) => {
                  e.preventDefault();
                  handleLinkClick('home');
                }}
              >
                Inicio
              </a>
            </li>
            <li>
              <a
                href="#productos"
                onClick={(e) => {
                  e.preventDefault();
                  handleLinkClick('products');
                }}
              >
                Productos
              </a>
            </li>
            <li>
              <a
                href="#nosotros"
                onClick={(e) => {
                  e.preventDefault();
                  handleLinkClick('about');
                }}
              >
                Nosotros
              </a>
            </li>
            <li>
              <a
                href="#contactos"
                onClick={(e) => {
                  e.preventDefault();
                  handleLinkClick('contact');
                }}
              >
                Contactos
              </a>
            </li>
          </ul>
        </div>

        {/* Columna Contacto */}
        <div className="footer-col">
          <h4 className="footer-title">Contacto</h4>
          <ul className="footer-contact">
            <li>
              <svg className="footer-icon-contact" viewBox="0 0 24 24">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                <polyline points="22,6 12,13 2,6" />
              </svg>
              <span>contacto@pedidos360.com</span>
            </li>
            <li>
              <svg className="footer-icon-contact" viewBox="0 0 24 24">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
              </svg>
              <span>+56 9 1234 5678</span>
            </li>
            <li>
              <svg className="footer-icon-contact" viewBox="0 0 24 24">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                <circle cx="12" cy="10" r="3" />
              </svg>
              <span>Santiago, Chile</span>
            </li>
          </ul>
        </div>
      </div>

      <div className="footer-bottom">
        <p>&copy; {currentYear} Pedidos360. Todos los derechos reservados.</p>
      </div>
    </footer>
  );
}