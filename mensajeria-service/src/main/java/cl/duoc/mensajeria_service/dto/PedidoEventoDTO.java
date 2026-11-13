package cl.duoc.mensajeria_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class PedidoEventoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventoId;
    private String tipoEvento;
    private String timestamp;
    private String origen;
    private PayloadPedido payload;

    public PedidoEventoDTO() {}

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public PayloadPedido getPayload() { return payload; }
    public void setPayload(PayloadPedido payload) { this.payload = payload; }

    public static class PayloadPedido implements Serializable {
        private Long pedidoId;
        private Long usuarioId;
        private String clienteEmail;
        private BigDecimal montoTotal;
        private String moneda;
        private List<ItemPedido> items;

        public PayloadPedido() {}

        public Long getPedidoId() { return pedidoId; }
        public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

        public Long getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

        public String getClienteEmail() { return clienteEmail; }
        public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

        public BigDecimal getMontoTotal() { return montoTotal; }
        public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }

        public String getMoneda() { return moneda; }
        public void setMoneda(String moneda) { this.moneda = moneda; }

        public List<ItemPedido> getItems() { return items; }
        public void setItems(List<ItemPedido> items) { this.items = items; }
    }

    public static class ItemPedido implements Serializable {
        private Long productoId;
        private String nombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;

        public ItemPedido() {}

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}