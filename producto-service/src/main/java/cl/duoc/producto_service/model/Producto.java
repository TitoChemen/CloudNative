package cl.duoc.producto_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es requerido")
    @Column(unique = true)
    private String codigo;

    @NotBlank(message = "El nombre es requerido")
    private String nombre; // En el front se mapea como 'title'

    @Column(length = 500)
    private String especificaciones; // En el front se mapea como 'specs'

    @DecimalMin(value = "0.0", message = "El precio debe ser positivo")
    private Double precio; // Precio actual (número limpio, ej: 1459990.0)

    private Double precioReferencial; // En el front 'refPrice' (ej: 1899990.0)

    private Integer descuento; // En el front 'discount' (ej: 23 para -23%)

    private String imagenUrl; // En el front 'img'

    private String badge; // En el front 'badge' (ej: "Agotado", "Oferta")

    @Min(0)
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "categoria_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Categoria categoria;
}