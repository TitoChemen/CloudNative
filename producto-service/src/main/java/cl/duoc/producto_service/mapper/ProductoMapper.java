package cl.duoc.producto_service.mapper;

import cl.duoc.producto_service.dto.ProductoDTO;
import cl.duoc.producto_service.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoDTO convertToDTO(Producto p) {
        if (p == null) return null;

        return ProductoDTO.builder()
                .id(p.getId())
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .especificaciones(p.getEspecificaciones())
                .precio(p.getPrecio())
                .precioReferencial(p.getPrecioReferencial())
                .descuento(p.getDescuento())
                .imagenUrl(p.getImagenUrl())
                .badge(p.getBadge())
                .stock(p.getStock())
                .nombreCategoria(p.getCategoria() != null ? p.getCategoria().getNombre() : "Sin categoría")
                .slugCategoria(p.getCategoria() != null ? p.getCategoria().getSlug() : "all")
                .build();
    }
}