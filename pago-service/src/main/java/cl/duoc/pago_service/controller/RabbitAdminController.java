package cl.duoc.pago_service.controller;

import cl.duoc.pago_service.dto.QueueDTO;
import cl.duoc.pago_service.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/queues")
public class RabbitAdminController {

    @Autowired
    private RabbitAdminService adminService;

    @PostMapping
    public ResponseEntity<?> crearCola(@Valid @RequestBody QueueDTO dto) {
        String mensaje = adminService.crearCola(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("estado", "OK", "mensaje", mensaje));
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<?> eliminarCola(@PathVariable String nombre) {
        boolean eliminada = adminService.eliminarCola(nombre);
        if (eliminada) {
            return ResponseEntity.ok(Map.of("estado", "OK", "mensaje", "Cola '" + nombre + "' eliminada correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("estado", "ERROR", "mensaje", "No se encontró o no se pudo eliminar la cola: " + nombre));
    }
}