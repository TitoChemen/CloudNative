package cl.duoc.usuario_service.controller;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/admin")
public class KafkaAdminController {

    @Autowired
    private ApplicationContext context;

    @PostMapping("/topics")
    public ResponseEntity<String> crearTopic(@RequestParam String nombreTopic, @RequestParam int particiones, @RequestParam short replicas) {
        try {
            KafkaAdmin kafkaAdmin = context.getBean(KafkaAdmin.class);
            try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
                NewTopic newTopic = new NewTopic(nombreTopic, particiones, replicas);
                adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            }
            return ResponseEntity.ok("Tópico '" + nombreTopic + "' creado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear tópico: " + e.getMessage());
        }
    }
}