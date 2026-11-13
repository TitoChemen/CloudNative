package cl.duoc.usuario_service.service;

import cl.duoc.usuario_service.dto.UsuarioDTO;
import cl.duoc.usuario_service.mapper.UsuarioMapper;
import cl.duoc.usuario_service.model.Usuario;
import cl.duoc.usuario_service.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testFindById_Success() {
        // Given
        Long id = 1L;
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Kevin");

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Kevin");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(u));
        when(usuarioMapper.toDTO(u)).thenReturn(dto);

        // When
        UsuarioDTO resultado = usuarioService.findById(id);

        // Then
        assertNotNull(resultado);
        assertEquals("Kevin", resultado.getNombre());
        verify(usuarioRepository, times(1)).findById(id);
    }

    @Test
    void testSaveUsuario_Success() {
        // Given
        Usuario u = new Usuario();
        when(usuarioRepository.save(u)).thenReturn(u);

        // When
        Usuario guardado = usuarioService.save(u);

        // Then
        assertNotNull(guardado);
        verify(usuarioRepository, times(1)).save(u);
    }
}