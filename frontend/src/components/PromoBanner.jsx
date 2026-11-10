import React, { useState, useEffect } from 'react';
import '../styles/PromoBanner.css';

// Configuración de diapositivas con enlaces directos a Imgur
const slides = [
  {
    centerPill: 'Cumplimos',
    centerTitleNum: '10',
    centerTitleUnit: 'años',
    centerSubtitleText: 'y seguimos ',
    centerSubtitleBold: 'actualizándonos_',
    centerVersion: 'v1.0',
    leftProduct: {
      img: 'https://i.imgur.com/IwA5RR5.png',
      title: 'Notebook Gamer Lenovo®',
      specs: 'Core i7 RTX 4060 16" WUXGA 16GB 1TB',
      currentPrice: '$1.459.990',
      refPrice: 'Referencial: $1.899.990',
      discount: '-23%'
    },
    rightProduct: {
      img: 'https://i.imgur.com/UkD6wX9.png',
      title: 'Notebook Gamer Gigabyte®',
      specs: 'Ryzen 9 RTX 4070 16" 32GB 1TB SSD',
      currentPrice: '$1.819.990',
      refPrice: 'Referencial: $1.969.000',
      discount: '-8%'
    }
  },
  {
    centerPill: 'Despacho',
    centerTitleNum: '24',
    centerTitleUnit: 'hrs',
    centerSubtitleText: 'envíos a todo ',
    centerSubtitleBold: 'Chile continental_',
    centerVersion: 'v2.0',
    leftProduct: {
      img: 'https://i.imgur.com/uXr4mMp.png',
      title: 'Notebook Gamer MSI Katana 15 B12V',
      specs: '15.6" FHD, i7 12650H, RTX 4070 8GB, RAM 16GB, SSD 512GB, W11H',
      currentPrice: '$1.799.990',
      refPrice: 'Referencial: $1.880.990',
      discount: '-4,31%'
    },
    rightProduct: {
      img: 'https://i.imgur.com/nhvsJAg.png',
      title: 'Notebook Gamer Lenovo Legion®',
      specs: 'Ryzen 7 RTX 4060 16GB 512GB',
      currentPrice: '$1.299.990',
      refPrice: 'Referencial: $1.599.990',
      discount: '-18%'
    }
  }
];

export default function PromoBanner() {
  const [current, setCurrent] = useState(0);
  const [isPlaying, setIsPlaying] = useState(true);

  const prevSlide = () => {
    setCurrent((prev) => (prev === 0 ? slides.length - 1 : prev - 1));
  };

  const nextSlide = () => {
    setCurrent((prev) => (prev === slides.length - 1 ? 0 : prev + 1));
  };

  useEffect(() => {
    if (!isPlaying) return;

    const timer = setInterval(() => {
      nextSlide();
    }, 5000);

    return () => clearInterval(timer);
  }, [current, isPlaying]);

  const slide = slides[current];

  return (
      <div className="banner-wrapper">
        {/* Barra superior verde */}
        <div className="banner-top-bar">
          <span>Únicos</span> con compra online y retiro <strong>inmediato</strong> en tienda Pedidos360 en todo Chile.
        </div>

        {/* Contenedor principal del banner */}
        <div className="banner-content">
          {/* Flecha izquierda */}
          <button
              type="button"
              className="banner-nav-btn prev-btn"
              onClick={prevSlide}
              aria-label="Anterior"
          >
            &#10094;
          </button>

          {/* Producto Izquierdo */}
          <div className="product-card">
            <div className="product-img-box">
              <img
                  src={slide.leftProduct.img}
                  alt={slide.leftProduct.title}
                  className="product-img"
              />
            </div>
            <div className="product-info">
              <h4 className="product-title">{slide.leftProduct.title}</h4>
              <p className="product-specs">{slide.leftProduct.specs}</p>
              <div className="price-tag-group">
                <div className="price-main">
                  <span className="current-price">{slide.leftProduct.currentPrice}</span>
                  <span className="ref-price">{slide.leftProduct.refPrice}</span>
                </div>
                <span className="discount-badge">{slide.leftProduct.discount}</span>
              </div>
            </div>
          </div>

          {/* Centro Promocional */}
          <div className="promo-center">
            <span className="promo-pill">{slide.centerPill}</span>
            <h2 className="promo-title">
              {slide.centerTitleNum} <span className="promo-unit">{slide.centerTitleUnit}</span>
            </h2>
            <p className="promo-subtitle">
              {slide.centerSubtitleText}<strong>{slide.centerSubtitleBold}</strong>
            </p>
            <div className="promo-chip">
              <span>{slide.centerVersion}</span>
            </div>
          </div>

          {/* Producto Derecho */}
          <div className="product-card">
            <div className="product-img-box">
              <img
                  src={slide.rightProduct.img}
                  alt={slide.rightProduct.title}
                  className="product-img"
              />
            </div>
            <div className="product-info">
              <h4 className="product-title">{slide.rightProduct.title}</h4>
              <p className="product-specs">{slide.rightProduct.specs}</p>
              <div className="price-tag-group">
                <div className="price-main">
                  <span className="current-price">{slide.rightProduct.currentPrice}</span>
                  <span className="ref-price">{slide.rightProduct.refPrice}</span>
                </div>
                <span className="discount-badge">{slide.rightProduct.discount}</span>
              </div>
            </div>
          </div>

          {/* Flecha derecha */}
          <button
              type="button"
              className="banner-nav-btn next-btn"
              onClick={nextSlide}
              aria-label="Siguiente"
          >
            &#10095;
          </button>

          {/* Botón de pausa */}
          <button
              type="button"
              className="banner-pause-btn"
              onClick={() => setIsPlaying(!isPlaying)}
              aria-label={isPlaying ? 'Pausar' : 'Reanudar'}
          >
            {isPlaying ? '❚❚' : '▶'}
          </button>
        </div>

        {/* Indicadores inferiores */}
        <div className="banner-dots">
          {slides.map((_, index) => (
              <button
                  key={index}
                  type="button"
                  className={`dot ${current === index ? 'active' : ''}`}
                  onClick={() => setCurrent(index)}
                  aria-label={`Ir a diapositiva ${index + 1}`}
              />
          ))}
        </div>
      </div>
  );
}