package cl.duoc.producto_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "categoria")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre; // Ej: "Notebooks", "Componentes"

    @Column(unique = true)
    private String slug; // Ej: "notebooks", "componentes", "perifericos"
}