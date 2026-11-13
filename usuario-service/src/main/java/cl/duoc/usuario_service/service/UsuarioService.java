package cl.duoc.usuario_service.service;

import cl.duoc.usuario_service.dto.UsuarioDTO;
import cl.duoc.usuario_service.mapper.UsuarioMapper;
import cl.duoc.usuario_service.model.Usuario;
import cl.duoc.usuario_service.repository.UsuarioRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private KafkaProducerService kafkaProducerService; // <-- 1. Inyectamos el productor de Kafka aquí

    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public UsuarioDTO findById(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        return usuarioMapper.toDTO(usuario);
    }

    public Usuario save(Usuario u){
        Usuario usuarioGuardado = usuarioRepository.save(u);
        
        // Publicar evento hacia la cola usuarios.queue (RabbitMQ)
        if (rabbitTemplate != null) {
            try {
                String mensaje = String.format("USUARIO_CREADO | ID: %d | Email: %s | Nombre: %s %s", 
                        usuarioGuardado.getId(), usuarioGuardado.getEmail(), usuarioGuardado.getNombre(), usuarioGuardado.getApellido());
                
                rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.routingKey", mensaje);
            } catch (Exception e) {
                System.err.println("No se pudo enviar el evento a RabbitMQ: " + e.getMessage());
            }
        }
        
        // 🚀 2. AQUÍ PUBLICAMOS EL EVENTO DE AUDITORÍA HACIA KAFKA (Cumple con el tópico obligatorio)
        if (kafkaProducerService != null) {
            try {
                String mensajeAuditoria = String.format("ACCION_REGISTRO: Usuario ID %d con email %s creado exitosamente", 
                        usuarioGuardado.getId(), usuarioGuardado.getEmail());
                
                kafkaProducerService.publicarEvento(
                        "auditoria.actividad",                 // Tópico exigido por la rúbrica
                        String.valueOf(usuarioGuardado.getId()), // Clave para las particiones
                        mensajeAuditoria                       // El contenido del evento de auditoría
                );
            } catch (Exception e) {
                System.err.println("No se pudo enviar el evento de auditoría a Kafka: " + e.getMessage());
            }
        }
        
        return usuarioGuardado;
    }

    public void delete(Long id){
        usuarioRepository.deleteById(id);
    }

    public Usuario update(Long id, Usuario usuario){
        Usuario usuarioActualizar = usuarioRepository.findById(id).orElse(null);
        if (usuarioActualizar == null) return null;
        usuarioActualizar.setNombre(usuario.getNombre());
        usuarioActualizar.setApellido(usuario.getApellido());
        usuarioActualizar.setRut(usuario.getRut());
        usuarioActualizar.setEmail(usuario.getEmail());
        usuarioActualizar.setDireccion(usuario.getDireccion());
        usuarioActualizar.setPassword(usuario.getPassword());

        return usuarioRepository.save(usuarioActualizar);
    }

    public boolean validarCredenciales(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> usuario.getPassword().equals(password))
                .orElse(false);
    }
}