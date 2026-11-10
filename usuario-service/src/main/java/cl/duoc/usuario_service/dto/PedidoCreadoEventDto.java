package cl.duoc.usuario_service.dto;

import java.time.LocalDateTime;

public class PedidoCreadoEventDto {
    private String eventoId;
    private LocalDateTime fechaHora;
    private Long usuarioId;
    private String correoUsuario;
    private DetallePedidoDto detallesPedido;
    private Double montoTotal;

    // Getters y Setters
    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
    public DetallePedidoDto getDetallesPedido() { return detallesPedido; }
    public void setDetallesPedido(DetallePedidoDto detallesPedido) { this.detallesPedido = detallesPedido; }
    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }
}

class DetallePedidoDto {
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;

    // Getters y Setters
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}