package cl.duoc.usuario_service.controller;

import cl.duoc.usuario_service.dto.LoginRequest;
import cl.duoc.usuario_service.dto.UsuarioDTO;
import cl.duoc.usuario_service.model.Usuario;
import cl.duoc.usuario_service.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario")
@Tag(name = "Gestión de Identidades (Usuarios)", description = "Sistema centralizado para el perfilamiento de entidades, control de acceso y aprovisionamiento seguro de cuentas.")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Auditoría de registro de usuarios", description = "Recupera la base de datos completa de entidades registradas en el ecosistema.")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido exitosamente.")
    @GetMapping
    public ResponseEntity<List<Usuario>> listar(){
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @Operation(summary = "Consultar perfil de usuario", description = "Realiza la recuperación de datos detallados de una entidad mediante su identificador único dentro del registro de identidades.")
    @ApiResponse(responseCode = "200", description = "Perfil de usuario localizado.")
    @ApiResponse(responseCode = "404", description = "Identidad no encontrada en el sistema.")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable("id") Long id){
        UsuarioDTO usuarioDTO = usuarioService.findById(id);
        if (usuarioDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioDTO);
    }

    @Operation(summary = "Provisionar nueva identidad digital", description = "Registra una nueva entidad en el ecosistema, asegurando la integridad de sus datos y su rol dentro del sistema.")
    @ApiResponse(responseCode = "201", description = "Identidad provisionada y persistida.")
    @PostMapping
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody Usuario usuario){
        Usuario usuarioNuevo = usuarioService.save(usuario);
        return new ResponseEntity<>(usuarioNuevo, HttpStatus.CREATED);
    }

    @Operation(summary = "Autenticar usuario", description = "Valida las credenciales ingresadas por el usuario para iniciar sesión.")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa.")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        boolean autenticado = usuarioService.validarCredenciales(request.getEmail(), request.getPassword());
        if (!autenticado) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o contraseña incorrectos");
        }
        return ResponseEntity.ok("Inicio de sesión exitoso");
    }

    @Operation(summary = "Revocar acceso de identidad", description = "Ejecuta la baja técnica de un usuario del sistema, invalidando sus permisos asociados.")
    @ApiResponse(responseCode = "204", description = "Identidad dada de baja correctamente.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id){
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Sincronizar actualización de perfil", description = "Modifica los atributos de una cuenta existente para reflejar cambios en el estado o perfil del usuario.")
    @ApiResponse(responseCode = "200", description = "Perfil de usuario sincronizado exitosamente.")
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable("id") Long id, @RequestBody Usuario usuario){
        Usuario usuarioActualizado = usuarioService.update(id, usuario);
        if (usuarioActualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(usuarioActualizado);
    }
}