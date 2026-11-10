import React, { useState } from 'react';
import '../styles/about.css';

const locationsData = {
  providencia: {
    name: 'Casa Matriz & Hub Express - Providencia',
    address: 'Av. Providencia 1234, Local 102 (Metro Manuel Montt)',
    hours: 'Lunes a Viernes: 09:00 - 19:30 | Sábados: 10:00 - 15:00',
    deliveryTime: 'Retiro express en 30 minutos / Despacho en 2 hrs',
    mapUrl: 'https://www.openstreetmap.org/export/embed.html?bbox=-70.6250%2C-33.4310%2C-70.6050%2C-33.4210&layer=mapnik&marker=-33.4260%2C-70.6150'
  },
  santiagoCentro: {
    name: 'Sucursal Centro & Logística - Santiago Centro',
    address: 'Paseo Huérfanos 835, Nivel 2 (Metro Plaza de Armas)',
    hours: 'Lunes a Viernes: 09:30 - 19:00 | Sábados: 10:30 - 14:30',
    deliveryTime: 'Retiro en 45 minutos / Despacho mismo día',
    mapUrl: 'https://www.openstreetmap.org/export/embed.html?bbox=-70.6550%2C-33.4440%2C-70.6400%2C-33.4340&layer=mapnik&marker=-33.4390%2C-70.6480'
  },
  lasCondes: {
    name: 'Punto Tech & Drop-off - Las Condes',
    address: 'Av. Apoquindo 4800, Piso 1 (Metro Escuela Militar)',
    hours: 'Lunes a Sábado: 10:00 - 20:00',
    deliveryTime: 'Retiro express en 30 minutos / Envíos prioritarios',
    mapUrl: 'https://www.openstreetmap.org/export/embed.html?bbox=-70.5880%2C-33.4220%2C-70.5680%2C-33.4100&layer=mapnik&marker=-33.4160%2C-70.5780'
  }
};

export default function About() {
  const [selectedHub, setSelectedHub] = useState('providencia');
  const currentHub = locationsData[selectedHub];

  return (
    <div className="about-container">
      {/* Hero Nosotros */}
      <header className="about-header">
        <span className="about-pill">10 Años Conectando Tecnología</span>
        <h2>Nuestra Historia en Pedidos<span>360</span></h2>
        <p>
          Nacidos en Santiago en 2016, comenzamos con la meta de acortar la brecha logística entre la compra de hardware gamer y su entrega en la puerta de los usuarios. Hoy somos un ecosistema e-commerce integral con despacho express en todo Chile continental.
        </p>
      </header>

      {/* Métricas y Logros */}
      <section className="about-stats-grid">
        <div className="stat-card">
          <span className="stat-num">+250K</span>
          <span className="stat-label">Pedidos Entregados</span>
          <p>Envíos garantizados y monitoreados en tiempo real de punta a punta.</p>
        </div>
        <div className="stat-card">
          <span className="stat-num">99.4%</span>
          <span className="stat-label">Satisfacción al Cliente</span>
          <p>Soporte técnico, garantías oficiales y validación estricta de productos.</p>
        </div>
        <div className="stat-card">
          <span className="stat-num">24 hrs</span>
          <span className="stat-label">Despacho en RM</span>
          <p>Flota logística dedicada para entregas express dentro del Gran Santiago.</p>
        </div>
        <div className="stat-card">
          <span className="stat-num">10 Años</span>
          <span className="stat-label">Trayectoria E-commerce</span>
          <p>Innovando desde startups locales hasta el retail especializado actual.</p>
        </div>
      </section>

      {/* Cronología de Hitos */}
      <section className="about-timeline-section">
        <h3>Nuestra Trayectoria</h3>
        <div className="about-timeline">
          <div className="timeline-item">
            <span className="timeline-year">2016</span>
            <h4>El Inicio</h4>
            <p>Comenzamos como una pequeña tienda boutique de ensamblaje a pedido en Providencia, gestionando envíos locales en bicicleta y furgones.</p>
          </div>
          <div className="timeline-item">
            <span className="timeline-year">2019</span>
            <h4>Plataforma Cloud & Automatización</h4>
            <p>Lanzamos nuestra primera versión de seguimiento 360° para que los clientes conocieran el estado de sus pedidos en vivo.</p>
          </div>
          <div className="timeline-item">
            <span className="timeline-year">2022</span>
            <h4>Cobertura Nacional</h4>
            <p>Alianzas con centros de distribución regionales para llegar a todas las regiones de Chile continental en menos de 48 horas.</p>
          </div>
          <div className="timeline-item">
            <span className="timeline-year">2026</span>
            <h4>Hubs Express Santiago</h4>
            <p>Consolidamos 3 puntos neurálgicos de retiro inmediato en Santiago con tiempos de entrega récord de 30 minutos.</p>
          </div>
        </div>
      </section>

      {/* Simulador de Ubicación / Hubs en Santiago */}
      <section className="about-locations-section">
        <div className="locations-heading">
          <h3>Simulador de Hubs y Retiros en Santiago</h3>
          <p>Elige tu punto de retiro o comuna más cercana para simular tiempos de entrega y disponibilidad.</p>
        </div>

        <div className="locations-selector">
          <button
            type="button"
            className={`loc-btn ${selectedHub === 'providencia' ? 'active' : ''}`}
            onClick={() => setSelectedHub('providencia')}
          >
            Providencia (Casa Matriz)
          </button>
          <button
            type="button"
            className={`loc-btn ${selectedHub === 'santiagoCentro' ? 'active' : ''}`}
            onClick={() => setSelectedHub('santiagoCentro')}
          >
            Santiago Centro
          </button>
          <button
            type="button"
            className={`loc-btn ${selectedHub === 'lasCondes' ? 'active' : ''}`}
            onClick={() => setSelectedHub('lasCondes')}
          >
            Las Condes
          </button>
        </div>

        <div className="simulator-grid">
          {/* Ficha técnica del Hub */}
          <div className="hub-details-card">
            <h4>{currentHub.name}</h4>
            <div className="hub-info-row">
              <span className="info-title">Dirección:</span>
              <span>{currentHub.address}</span>
            </div>
            <div className="hub-info-row">
              <span className="info-title">Horario de Atención:</span>
              <span>{currentHub.hours}</span>
            </div>
            <div className="hub-info-row">
              <span className="info-title">Tiempo Estimado:</span>
              <span className="delivery-highlight">{currentHub.deliveryTime}</span>
            </div>
            <div className="hub-badge-live">
              <span className="pulse-dot"></span>
              <span>Hub operativo para retiro inmediato</span>
            </div>
          </div>

          {/* Mapa embebido de OpenStreetMap */}
          <div className="hub-map-wrapper">
            <iframe
              title={currentHub.name}
              src={currentHub.mapUrl}
              className="hub-map-frame"
              loading="lazy"
            />
          </div>
        </div>
      </section>
    </div>
  );
}