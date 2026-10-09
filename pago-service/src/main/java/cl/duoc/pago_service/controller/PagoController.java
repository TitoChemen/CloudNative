package cl.duoc.pago_service.controller;

import cl.duoc.pago_service.dto.PagoRequest;
import cl.duoc.pago_service.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody PagoRequest request) {
        Map<String, Object> resultado = pagoService.procesarPago(request);
        return ResponseEntity.ok(resultado);
    }
}