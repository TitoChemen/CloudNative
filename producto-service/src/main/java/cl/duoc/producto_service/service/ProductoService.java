package cl.duoc.producto_service.service;

import cl.duoc.producto_service.dto.ProductoDTO;
import cl.duoc.producto_service.mapper.ProductoMapper;
import cl.duoc.producto_service.model.Categoria;
import cl.duoc.producto_service.model.Producto;
import cl.duoc.producto_service.repository.CategoriaRepository;
import cl.duoc.producto_service.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.principal:productos.exchange}")
    private String exchangeProductos;

    @Value("${rabbitmq.routingkeys.principal:productos.routingKey}")
    private String routingKeyProductos;

    @Value("${notificaciones.email-admin:offgdev@gmail.com}")
    private String emailAdmin;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoMapper productoMapper;

    private Producto obtenerEntidad(String criterio) {
        Producto p = productoRepository.findByCodigo(criterio);
        if (p == null) {
            try {
                Long id = Long.parseLong(criterio);
                p = productoRepository.findById(id).orElse(null);
            } catch (NumberFormatException ignored) {
            }
        }
        return p;
    }

    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(productoMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO buscarPorCodigo(String criterio) {
        Producto p = obtenerEntidad(criterio);
        return (p != null) ? productoMapper.convertToDTO(p) : null;
    }

    public Producto save(Producto p) {
        if (p.getCategoria() != null && p.getCategoria().getId() != null) {
            Categoria cat = categoriaRepository.findById(p.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada, revisa el ID."));
            p.setCategoria(cat);
        }
        return productoRepository.save(p);
    }

    public ProductoDTO actualizar(String criterio, Producto productoDetalles) {
        Producto existente = obtenerEntidad(criterio);
        if (existente == null) {
            return null;
        }

        if (productoDetalles.getNombre() != null) existente.setNombre(productoDetalles.getNombre());
        if (productoDetalles.getPrecio() != null) existente.setPrecio(productoDetalles.getPrecio());
        Integer stockAnterior = existente.getStock();
        if (productoDetalles.getStock() != null) existente.setStock(productoDetalles.getStock());

        if (productoDetalles.getCategoria() != null && productoDetalles.getCategoria().getId() != null) {
            Categoria cat = categoriaRepository.findById(productoDetalles.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada, revisa el ID."));
            existente.setCategoria(cat);
        }

        Producto guardado = productoRepository.save(existente);
        publicarEventoStock(guardado, stockAnterior);
        return productoMapper.convertToDTO(guardado);
    }

    @Transactional
    public boolean deleteByCodigo(String criterio) {
        Producto p = obtenerEntidad(criterio);
        if (p == null) {
            return false;
        }
        productoRepository.delete(p);
        return true;
    }

    /**
     * Publica un evento en RabbitMQ cuando el stock cambia. Si RabbitMQ falla,
     * solo se registra el error: la actualización del producto no se ve afectada.
     */
    private void publicarEventoStock(Producto p, Integer stockAnterior) {
        if (Objects.equals(stockAnterior, p.getStock())) {
            return;
        }
        String tipo = (p.getStock() != null && p.getStock() == 0) ? "STOCK_AGOTADO" : "STOCK_ACTUALIZADO";
        String mensaje = String.format("%s | Producto: %s | Stock anterior: %s | Stock actual: %s | Email: %s",
                tipo, p.getNombre(), stockAnterior, p.getStock(), emailAdmin);
        try {
            rabbitTemplate.convertAndSend(exchangeProductos, routingKeyProductos, mensaje);
            log.info("[RabbitMQ] Evento publicado: {}", mensaje);
        } catch (Exception e) {
            log.error("[RabbitMQ] No se pudo publicar el evento de stock: {}", e.getMessage());
        }
    }
}
