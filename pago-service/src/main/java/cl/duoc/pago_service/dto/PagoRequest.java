package cl.duoc.pago_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class PagoRequest {
    private String pedidoId;
    private Double monto;
    private String tarjeta;
    private String email;
    private List<PedidoEventoDTO.ItemPedido> items;
}