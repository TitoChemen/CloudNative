package cl.duoc.producto_service.consumer;

import cl.duoc.producto_service.dto.PedidoEventoDTO;
import cl.duoc.producto_service.model.Producto;
import cl.duoc.producto_service.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoProductoConsumerTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private PedidoProductoConsumer consumer;

    @Test
    void debeDescontarStockSoloUnaVezParaPagoAprobadoEIgnorarRechazado() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Notebook Gamer Lenovo LOQ");
        producto.setStock(10);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        PedidoEventoDTO.ItemPedido item = new PedidoEventoDTO.ItemPedido();
        item.setProductoId(1L);
        item.setNombre("Notebook Gamer Lenovo LOQ");
        item.setCantidad(2);

        PedidoEventoDTO.PayloadPedido payload = new PedidoEventoDTO.PayloadPedido();
        payload.setPedidoId(295885L);
        payload.setItems(List.of(item));

        PedidoEventoDTO eventoAprobado = construirEvento(
                "evt-001", "PAGO_APROBADO", "2026-11-15T10:00:00Z", "pago-service", payload);
        consumer.procesarActualizacionStock(eventoAprobado);
        consumer.procesarActualizacionStock(eventoAprobado); // Duplicado: omitido por idempotencia

        PedidoEventoDTO eventoRechazado = construirEvento(
                "evt-002", "PAGO_RECHAZADO", "2026-11-15T10:05:00Z", "pago-service", payload);
        consumer.procesarActualizacionStock(eventoRechazado);

        assertEquals(8, producto.getStock());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void debeLanzarExcepcionYNoGuardarCuandoStockEsInsuficiente() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Notebook Gamer Lenovo LOQ");
        producto.setStock(1);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        PedidoEventoDTO.ItemPedido item = new PedidoEventoDTO.ItemPedido();
        item.setProductoId(1L);
        item.setNombre("Notebook Gamer Lenovo LOQ");
        item.setCantidad(5);

        PedidoEventoDTO.PayloadPedido payload = new PedidoEventoDTO.PayloadPedido();
        payload.setPedidoId(295886L);
        payload.setItems(List.of(item));

        PedidoEventoDTO eventoSinStock = construirEvento(
                "evt-003", "PAGO_APROBADO", "2026-11-15T10:10:00Z", "pago-service", payload);

        assertThrows(
                IllegalStateException.class,
                () -> consumer.procesarActualizacionStock(eventoSinStock)
        );

        verify(productoRepository, never()).save(producto);
    }

    private PedidoEventoDTO construirEvento(
            String eventoId,
            String tipoEvento,
            String timestamp,
            String origen,
            PedidoEventoDTO.PayloadPedido payload) {
        PedidoEventoDTO dto = new PedidoEventoDTO();
        dto.setEventoId(eventoId);
        dto.setTipoEvento(tipoEvento);
        dto.setTimestamp(timestamp);
        dto.setOrigen(origen);
        dto.setPayload(payload);
        return dto;
    }
}