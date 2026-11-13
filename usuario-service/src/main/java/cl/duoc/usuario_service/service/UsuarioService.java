package cl.duoc.usuario_service.service;

import cl.duoc.usuario_service.dto.UsuarioDTO;
import cl.duoc.usuario_service.mapper.UsuarioMapper;
import cl.duoc.usuario_service.model.Usuario;
import cl.duoc.usuario_service.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private KafkaProducerService kafkaProducerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        return usuarioMapper.toDTO(usuario);
    }

    public Usuario save(Usuario u) {
        Usuario usuarioGuardado = usuarioRepository.save(u);

        // 1. Publicar evento hacia RabbitMQ
        if (rabbitTemplate != null) {
            try {
                String mensaje = String.format("USUARIO_CREADO | ID: %d | Email: %s | Nombre: %s %s",
                        usuarioGuardado.getId(), usuarioGuardado.getEmail(),
                        usuarioGuardado.getNombre(), usuarioGuardado.getApellido());
                rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.routingKey", mensaje);
            } catch (Exception e) {
                log.error("No se pudo enviar el evento a RabbitMQ: {}", e.getMessage());
            }
        }

        // 2. Publicar evento de auditoría en Kafka
        enviarAuditoriaKafka("USUARIO_CREADO", usuarioGuardado.getId(), usuarioGuardado.getEmail());

        return usuarioGuardado;
    }

    public Usuario update(Long id, Usuario usuario) {
        Usuario usuarioActualizar = usuarioRepository.findById(id).orElse(null);
        if (usuarioActualizar == null) return null;

        usuarioActualizar.setNombre(usuario.getNombre());
        usuarioActualizar.setApellido(usuario.getApellido());
        usuarioActualizar.setRut(usuario.getRut());
        usuarioActualizar.setEmail(usuario.getEmail());
        usuarioActualizar.setDireccion(usuario.getDireccion());
        usuarioActualizar.setPassword(usuario.getPassword());

        Usuario actualizado = usuarioRepository.save(usuarioActualizar);
        enviarAuditoriaKafka("USUARIO_ACTUALIZADO", actualizado.getId(), actualizado.getEmail());
        return actualizado;
    }

    public void delete(Long id) {
        usuarioRepository.findById(id).ifPresent(u -> {
            usuarioRepository.deleteById(id);
            enviarAuditoriaKafka("USUARIO_ELIMINADO", u.getId(), u.getEmail());
        });
    }

    public boolean validarCredenciales(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> {
                    boolean valido = usuario.getPassword().equals(password);
                    enviarAuditoriaKafka(
                            valido ? "LOGIN_EXITOSO" : "LOGIN_FALLIDO",
                            usuario.getId(),
                            usuario.getEmail()
                    );
                    return valido;
                })
                .orElse(false);
    }

    private void enviarAuditoriaKafka(String accion, Long usuarioId, String email) {
        if (kafkaProducerService == null) {
            log.warn("KafkaProducerService no está disponible; evento {} omitido.", accion);
            return;
        }
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("accion", accion);
            payload.put("usuarioId", usuarioId);
            payload.put("email", email);
            payload.put("timestamp", Instant.now().toString());

            String jsonEvento = objectMapper.writeValueAsString(payload);
            kafkaProducerService.publicarEvento(
                    "auditoria.actividad",
                    String.valueOf(usuarioId),
                    jsonEvento
            );
        } catch (Exception e) {
            log.error("No se pudo enviar evento de auditoría a Kafka: {}", e.getMessage());
        }
    }
}