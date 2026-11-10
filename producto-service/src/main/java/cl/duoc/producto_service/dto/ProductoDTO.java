package cl.duoc.producto_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String especificaciones;
    private Double precio;
    private Double precioReferencial;
    private Integer descuento;
    private String imagenUrl;
    private String badge;
    private Integer stock;
    private String nombreCategoria;
    private String slugCategoria;
}