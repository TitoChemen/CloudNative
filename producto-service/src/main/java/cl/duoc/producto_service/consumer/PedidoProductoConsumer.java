package cl.duoc.producto_service.consumer;

import cl.duoc.producto_service.dto.PedidoEventoDTO;
import cl.duoc.producto_service.model.Producto;
import cl.duoc.producto_service.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PedidoProductoConsumer {

    private static final Logger log = LoggerFactory.getLogger(PedidoProductoConsumer.class);

    private final ProductoRepository productoRepository;
    private final Set<String> eventosProcesados = ConcurrentHashMap.newKeySet();

    public PedidoProductoConsumer(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional
    @KafkaListener(
        topics = "pedidos.eventos",
        groupId = "producto-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void procesarActualizacionStock(@Payload PedidoEventoDTO evento) {
        log.info("[PRODUCTO-SERVICE] Evento recibido. ID: {} | Tipo: {}",
                evento.getEventoId(), evento.getTipoEvento());

        if (!"PAGO_APROBADO".equalsIgnoreCase(evento.getTipoEvento())) {
            log.info("[PRODUCTO-SERVICE] Evento ignorado (no es PAGO_APROBADO): {}", evento.getTipoEvento());
            return;
        }

        if (evento.getEventoId() != null && !eventosProcesados.add(evento.getEventoId())) {
            log.warn("[PRODUCTO-SERVICE] Evento duplicado omitido (idempotencia): {}", evento.getEventoId());
            return;
        }

        if (evento.getPayload() == null || evento.getPayload().getItems() == null || evento.getPayload().getItems().isEmpty()) {
            log.warn("[PRODUCTO-SERVICE] Evento omitido: No se incluyeron ítems en el payload.");
            return;
        }

        for (PedidoEventoDTO.ItemPedido item : evento.getPayload().getItems()) {
            Long productoId = item.getProductoId();
            Integer cantidadAComprar = item.getCantidad();

            Optional<Producto> productoOpt = productoRepository.findById(productoId);
            if (productoOpt.isEmpty()) {
                log.error("[PRODUCTO-SERVICE ERROR] Producto ID {} no existe en el catálogo.", productoId);
                throw new IllegalArgumentException("Producto no encontrado en BD: " + productoId);
            }

            Producto producto = productoOpt.get();
            if (producto.getStock() < cantidadAComprar) {
                log.error("[PRODUCTO-SERVICE ERROR] Stock insuficiente para Producto ID {}. Stock actual: {}, Requerido: {}",
                        productoId, producto.getStock(), cantidadAComprar);
                throw new IllegalStateException("Stock insuficiente para el producto ID: " + productoId);
            }

            int stockAnterior = producto.getStock();
            int nuevoStock = stockAnterior - cantidadAComprar;
            producto.setStock(nuevoStock);
            productoRepository.save(producto);

            log.info("[PRODUCTO-SERVICE SUCCESS] Stock actualizado Producto ID {}. Anterior: {} -> Nuevo: {}",
                    productoId, stockAnterior, nuevoStock);
        }

        log.info("[PRODUCTO-SERVICE SUCCESS] Descuento de stock finalizado para Pedido ID: {}",
                evento.getPayload().getPedidoId());
    }
}