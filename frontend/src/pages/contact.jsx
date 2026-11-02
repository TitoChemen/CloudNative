import React, { useState } from 'react';
import '../styles/contact.css';

const contactNumbers = [
  {
    comuna: 'Providencia (Casa Matriz)',
    badge: 'Central RM',
    horarioSemana: 'Lunes a Viernes: 08:30 a 19:30 hrs',
    horarioSabado: 'Sábados: 10:00 a 15:00 hrs',
    telefonos: [
      { tipo: 'Mesa Central / Ventas', numero: '+56 2 2840 5101' },
      { tipo: 'Soporte y Garantías', numero: '+56 2 2840 5102' },
      { tipo: 'Coordinación Retiros Express', numero: '+56 9 9123 4561' }
    ]
  },
  {
    comuna: 'Santiago Centro (Hub Logístico)',
    badge: 'Despachos Express',
    horarioSemana: 'Lunes a Viernes: 09:00 a 19:00 hrs',
    horarioSabado: 'Sábados: 10:30 a 14:30 hrs',
    telefonos: [
      { tipo: 'Atención Clientes Centro', numero: '+56 2 2630 4201' },
      { tipo: 'Consultas Facturación & RUT', numero: '+56 2 2630 4202' },
      { tipo: 'Móvil de Seguimiento En Ruta', numero: '+56 9 9234 5672' }
    ]
  },
  {
    comuna: 'Las Condes (Punto Tech)',
    badge: 'Atención Empresas',
    horarioSemana: 'Lunes a Viernes: 09:00 a 20:00 hrs',
    horarioSabado: 'Sábados: 10:00 a 18:00 hrs',
    telefonos: [
      { tipo: 'Ventas Corporativas & B2B', numero: '+56 2 2410 8301' },
      { tipo: 'Postventa y Devoluciones', numero: '+56 2 2410 8302' },
      { tipo: 'Línea de Urgencia Hardware', numero: '+56 9 9345 6783' }
    ]
  }
];

const socialLinks = [
  {
    name: 'Instagram',
    handle: '@pedidos360_cl',
    desc: 'Noticias, promociones y lanzamientos diarios.',
    color: '#e1306c',
    url: '#instagram',
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="2" y="2" width="20" height="20" rx="5" ry="5" />
        <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z" />
        <line x1="17.5" y1="6.5" x2="17.51" y2="6.5" />
      </svg>
    )
  },
  {
    name: 'X (Twitter)',
    handle: '@Pedidos360CL',
    desc: 'Alertas de stock y estado del servicio en tiempo real.',
    color: 'var(--text-main)', // Se adapta al tema
    url: '#x',
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z" />
      </svg>
    )
  },
  {
    name: 'Facebook',
    handle: '/Pedidos360Chile',
    desc: 'Comunidad, eventos gaming y reseñas de hardware.',
    color: '#1877f2',
    url: '#facebook',
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
      </svg>
    )
  },
  {
    name: 'WhatsApp',
    handle: '+56 9 1234 5678',
    desc: 'Atención rápida para estado de compras online.',
    color: '#25d366',
    url: '#whatsapp',
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M12.031 6.172c-3.181 0-5.767 2.586-5.768 5.766-.001 1.298.38 2.27 1.019 3.287l-.711 2.598 2.664-.698c.95.519 1.771.815 2.796.815 3.182 0 5.767-2.587 5.768-5.766.002-3.182-2.585-5.768-5.768-5.768zm3.392 8.163c-.141.398-.711.758-1.011.802-.279.04-.643.063-1.897-.456-1.571-.65-2.58-2.246-2.658-2.35-.078-.104-.639-.851-.639-1.625 0-.773.407-1.155.552-1.313.146-.157.319-.196.425-.196.106 0 .212.001.305.006.098.005.23-.037.36.275.133.319.453 1.104.493 1.184.04.08.066.173.013.28-.053.106-.08.172-.159.265-.08.093-.167.208-.239.279-.08.08-.163.167-.07.327.093.16.413.681.887 1.103.61.543 1.124.711 1.284.791.16.08.253.067.346-.04.093-.106.399-.465.505-.624.106-.16.213-.133.36-.08.146.053.931.439 1.09.519.16.08.266.12.306.186.04.067.04.386-.101.784zM12.012 2C6.49 2 2.012 6.478 2.012 12c0 2.21.723 4.253 1.944 5.91L2 22l4.227-1.895A9.949 9.949 0 0 0 12.012 22c5.523 0 10-4.478 10-10s-4.477-10-10-10z" />
      </svg>
    )
  },
  {
    name: 'Discord',
    handle: 'discord.gg/pedidos360',
    desc: 'Canal de ofertas exclusivas y setups gamers.',
    color: '#5865f2',
    url: '#discord',
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M20.317 4.37a19.791 19.791 0 0 0-4.885-1.515.074.074 0 0 0-.079.037c-.21.375-.444.864-.608 1.25a18.27 18.27 0 0 0-5.487 0 12.64 12.64 0 0 0-.617-1.25.077.077 0 0 0-.079-.037A19.736 19.736 0 0 0 3.677 4.37a.07.07 0 0 0-.032.027C.533 9.046-.32 13.58.099 18.057a.082.082 0 0 0 .031.057 19.9 19.9 0 0 0 5.993 3.03.078.078 0 0 0 .084-.028 14.09 14.09 0 0 0 1.226-1.994.076.076 0 0 0-.041-.106 13.107 13.107 0 0 1-1.872-.892.077.077 0 0 1-.008-.128 10.2 10.2 0 0 0 .372-.292.074.074 0 0 1 .077-.01c3.928 1.793 8.18 1.793 12.061 0a.074.074 0 0 1 .078.01c.12.098.246.198.373.292a.077.077 0 0 1-.006.127 12.299 12.299 0 0 1-1.873.894.077.077 0 0 0-.041.107c.36.698.772 1.362 1.225 1.993a.076.076 0 0 0 .084.028 19.839 19.839 0 0 0 6.002-3.03.077.077 0 0 0 .032-.054c.5-5.177-.838-9.674-3.549-13.66a.061.061 0 0 0-.031-.028zM8.02 15.33c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.956-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.956 2.418-2.157 2.418zm7.975 0c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.955-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.946 2.418-2.157 2.418z" />
      </svg>
    )
  },
  {
    name: 'Threads',
    handle: '@pedidos360_cl',
    desc: 'Debates sobre nuevas tecnologías y GPUs.',
    color: 'var(--text-main)', // Se adapta al tema
    url: '#threads',
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M12.001 2c-5.522 0-10 4.477-10 10s4.478 10 10 10c4.137 0 7.7-2.528 9.21-6.136a.75.75 0 0 0-1.385-.578C18.528 18.067 15.485 20.5 12 20.5c-4.694 0-8.5-3.806-8.5-8.5 0-4.694 3.806-8.5 8.5-8.5 4.672 0 8.416 3.738 8.498 8.409v.678c0 1.621-1.168 2.663-2.618 2.663-1.09 0-1.928-.684-2.18-1.577.892-.614 1.488-1.636 1.488-2.798 0-1.892-1.536-3.428-3.428-3.428s-3.429 1.536-3.429 3.428c0 1.893 1.536 3.428 3.429 3.428.847 0 1.624-.308 2.226-.821.439.882 1.341 1.47 2.424 1.47 2.247 0 4.118-1.616 4.118-4.045v-.687C22.408 6.577 17.728 2 12.001 2zm0 6.953c1.066 0 1.929.863 1.929 1.929s-.863 1.928-1.929 1.928c-1.065 0-1.929-.862-1.929-1.928s.864-1.929 1.929-1.929z" />
      </svg>
    )
  },
  {
    name: 'Telegram',
    handle: 't.me/pedidos360_ofertas',
    desc: 'Grupo de liquidaciones y stock relámpago.',
    color: '#229ed9',
    url: '#telegram',
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M11.944 0A12 12 0 0 0 0 12a12 12 0 0 0 12 12 12 12 0 0 0 12-12A12 12 0 0 0 12 0a12 12 0 0 0-.056 0zm4.962 7.224c.1-.002.321.023.465.14a.506.506 0 0 1 .171.325c.016.093.036.306.02.472-.18 1.898-.962 6.502-1.36 8.627-.168.9-.499 1.201-.82 1.23-.696.065-1.225-.46-1.9-.902-1.056-.693-1.653-1.124-2.678-1.8-1.185-.78-.417-1.21.258-1.91.177-.184 3.247-2.977 3.307-3.23.007-.032.014-.15-.056-.212s-.174-.041-.249-.024c-.106.024-1.793 1.14-5.061 3.345-.48.33-.913.49-1.302.48-.428-.008-1.252-.241-1.865-.44-.752-.245-1.349-.374-1.297-.789.027-.216.325-.437.893-.663 3.498-1.524 5.83-2.529 6.998-3.014 3.332-1.386 4.025-1.627 4.476-1.635z" />
      </svg>
    )
  },
  {
    name: 'TikTok',
    handle: '@pedidos360',
    desc: 'Unboxings de notebooks gamer y benchmarks.',
    color: 'var(--text-main)', // Se adapta al tema
    url: '#tiktok',
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor">
        <path d="M12.525.02c1.31-.02 2.61-.01 3.91-.02.08 1.53.63 3.09 1.75 4.17 1.12 1.11 2.7 1.62 4.24 1.79v4.03c-1.44-.05-2.89-.35-4.2-.97-.57-.26-1.1-.59-1.62-.93-.01 2.92.01 5.84-.02 8.75-.08 1.4-.54 2.79-1.35 3.94-1.31 1.92-3.58 3.17-5.91 3.21-1.43.08-2.86-.31-4.08-1.03-2.02-1.19-3.44-3.37-3.65-5.71-.02-.5-.03-1-.01-1.49.18-1.9 1.12-3.72 2.58-4.96 1.66-1.44 3.98-2.13 6.15-1.72.02 1.48-.04 2.96-.04 4.44-.99-.32-2.15-.23-3.02.37-.63.41-1.11 1.04-1.36 1.75-.21.51-.24 1.07-.14 1.61.24 1.64 1.82 2.89 3.5 2.77 1.81-.03 3.32-1.52 3.42-3.33.05-3.39.02-6.78.03-10.17V.02h.81z" />
      </svg>
    )
  }
];

export default function Contact() {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    comuna: 'Providencia',
    asunto: '',
    mensaje: ''
  });
  const [sent, setSent] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setSent(true);
    setTimeout(() => {
      alert(`¡Gracias ${formData.nombre}! Tu mensaje ha sido recibido. Te responderemos al correo: ${formData.email}`);
      setSent(false);
      setFormData({ nombre: '', email: '', comuna: 'Providencia', asunto: '', mensaje: '' });
    }, 500);
  };

  return (
    <div className="contact-wrapper">
      <header className="contact-header">
        <span className="contact-pill">Canales de Atención 360°</span>
        <h2>Canales de Contacto & Redes Oficiales</h2>
        <p>
          Comunícate directamente con nuestros especialistas a través de nuestras líneas telefónicas comunales, redes oficiales o envíanos un requerimiento directo.
        </p>
      </header>

      {/* 1. Teléfonos por Comuna y Horarios */}
      <section className="contact-section">
        <div className="section-title-box">
          <h3>Central Telefónica por Comunas de Santiago</h3>
          <p>Líneas dedicadas para retiro inmediato, soporte y ventas en cada sucursal.</p>
        </div>

        <div className="comunas-grid">
          {contactNumbers.map((item, index) => (
            <div key={index} className="comuna-card">
              <div className="comuna-card-header">
                <h4>{item.comuna}</h4>
                <span className="comuna-badge">{item.badge}</span>
              </div>

              <div className="comuna-horarios">
                <div className="horario-row">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10" />
                    <polyline points="12 6 12 12 16 14" />
                  </svg>
                  <span>{item.horarioSemana}</span>
                </div>
                <div className="horario-row">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10" />
                    <polyline points="12 6 12 12 16 14" />
                  </svg>
                  <span>{item.horarioSabado}</span>
                </div>
              </div>

              <div className="comuna-telefonos">
                {item.telefonos.map((tel, idx) => (
                  <a key={idx} href={`tel:${tel.numero.replace(/\s+/g, '')}`} className="phone-link">
                    <div className="phone-icon-box">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
                      </svg>
                    </div>
                    <div className="phone-data">
                      <span className="phone-type">{tel.tipo}</span>
                      <strong className="phone-num">{tel.numero}</strong>
                    </div>
                  </a>
                ))}
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 2. Redes Sociales Oficiales */}
      <section className="contact-section">
        <div className="section-title-box">
          <h3>Redes Sociales y Comunidad</h3>
          <p>Conéctate con nuestra comunidad gamer y entérate de descuentos y liquidaciones antes que nadie.</p>
        </div>

        <div className="socials-grid">
          {socialLinks.map((social, index) => (
            <a
              key={index}
              href={social.url}
              className="social-card"
              target="_blank"
              rel="noopener noreferrer"
            >
              <div className="social-icon-wrapper" style={{ color: social.color }}>
                {social.icon}
              </div>
              <div className="social-info">
                <div className="social-name-row">
                  <h4>{social.name}</h4>
                  <span className="social-handle">{social.handle}</span>
                </div>
                <p>{social.desc}</p>
              </div>
            </a>
          ))}
        </div>
      </section>

      {/* 3. Formulario de Contacto Rápido */}
      <section className="contact-form-section">
        <div className="contact-form-card">
          <div className="form-header">
            <h3>Envíanos un Mensaje</h3>
            <p>¿Tienes una duda técnica o consulta sobre tu compra? Te responderemos a la brevedad.</p>
          </div>

          <form onSubmit={handleSubmit} className="contact-form">
            <div className="contact-form-row">
              <div className="contact-input-field">
                <label htmlFor="cnt-nombre">Nombre y Apellido *</label>
                <input
                  id="cnt-nombre"
                  type="text"
                  name="nombre"
                  placeholder="Carlos Muñoz"
                  value={formData.nombre}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="contact-input-field">
                <label htmlFor="cnt-email">Correo Electrónico *</label>
                <input
                  id="cnt-email"
                  type="email"
                  name="email"
                  placeholder="carlos@gmail.com"
                  value={formData.email}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="contact-form-row">
              <div className="contact-input-field">
                <label htmlFor="cnt-comuna">Sucursal / Comuna de Interés</label>
                <select
                  id="cnt-comuna"
                  name="comuna"
                  value={formData.comuna}
                  onChange={handleChange}
                >
                  <option value="Providencia">Providencia (Casa Matriz)</option>
                  <option value="Santiago Centro">Santiago Centro</option>
                  <option value="Las Condes">Las Condes</option>
                  <option value="Regiones">Otra Región / Despacho Nacional</option>
                </select>
              </div>
              <div className="contact-input-field">
                <label htmlFor="cnt-asunto">Asunto *</label>
                <input
                  id="cnt-asunto"
                  type="text"
                  name="asunto"
                  placeholder="Consulta sobre garantía o despacho"
                  value={formData.asunto}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="contact-input-field">
              <label htmlFor="cnt-mensaje">Mensaje *</label>
              <textarea
                id="cnt-mensaje"
                name="mensaje"
                rows="4"
                placeholder="Escribe aquí los detalles de tu consulta..."
                value={formData.mensaje}
                onChange={handleChange}
                required
              />
            </div>

            <button type="submit" className="contact-submit-btn" disabled={sent}>
              {sent ? 'Enviando requerimiento...' : 'Enviar Mensaje Directo'}
            </button>
          </form>
        </div>
      </section>
    </div>
  );
}