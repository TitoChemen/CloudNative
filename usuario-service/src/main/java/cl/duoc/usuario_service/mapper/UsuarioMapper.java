package cl.duoc.usuario_service.mapper;

import cl.duoc.usuario_service.dto.UsuarioDTO;
import cl.duoc.usuario_service.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario usuario){
        if (usuario == null) return null;

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setRut(usuario.getRut());
        dto.setEmail(usuario.getEmail());
        dto.setDireccion(usuario.getDireccion());

        return dto;
    }
}