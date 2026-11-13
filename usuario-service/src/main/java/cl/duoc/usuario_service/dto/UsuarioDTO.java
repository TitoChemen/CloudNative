package cl.duoc.usuario_service.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String email;
    private String direccion;
    private String password;
}