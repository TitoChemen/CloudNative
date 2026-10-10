package cl.duoc.pago_service.dto;

import lombok.Data;

@Data
public class PagoRequest {
    private String pedidoId;
    private Double monto;
    private String tarjeta;
    private String email; // Correo dinámico del comprador
}