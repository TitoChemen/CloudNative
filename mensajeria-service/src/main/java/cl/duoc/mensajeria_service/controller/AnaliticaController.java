package cl.duoc.mensajeria_service.controller;

import cl.duoc.mensajeria_service.config.AnaliticaStreamsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analitica")
@RequiredArgsConstructor
public class AnaliticaController {

    private final AnaliticaStreamsConfig analiticaStreams;

    @GetMapping("/resumen")
    public ResponseEntity> obtenerResumenTiempoReal() {
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("pagosAprobados", analiticaStreams.getPagosAprobados().get());
        resumen.put("pagosRechazados", analiticaStreams.getPagosRechazados().get());
        resumen.put("montoTotalVentas", analiticaStreams.getMontoTotalVentas().sum());
        resumen.put("eventosAuditoria", analiticaStreams.getEventosAuditoria().get());
        resumen.put("ultimosEventos", analiticaStreams.getUltimosEventos());
        return ResponseEntity.ok(resumen);
    }
}