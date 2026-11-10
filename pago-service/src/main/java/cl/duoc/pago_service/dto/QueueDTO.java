package cl.duoc.pago_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class QueueDTO {

    @NotBlank(message = "El nombre de la cola es obligatorio y no puede estar vacío")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "El nombre de la cola solo puede contener letras, números, puntos, guiones y guiones bajos"
    )
    private String nombreCola;

    private boolean durable = true;

    public QueueDTO() {}

    public QueueDTO(String nombreCola, boolean durable) {
        this.nombreCola = nombreCola;
        this.durable = durable;
    }

    public String getNombreCola() {
        return nombreCola;
    }

    public void setNombreCola(String nombreCola) {
        this.nombreCola = nombreCola;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }
}