package cl.duoc.mensajeria_service.controller;

import cl.duoc.mensajeria_service.config.AnaliticaStreamsConfig;
import cl.duoc.mensajeria_service.dto.ResumenAnaliticaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mensajeria/analitica")
@RequiredArgsConstructor
public class AnaliticaController {

    private final AnaliticaStreamsConfig analiticaStreams;

    @GetMapping("/resumen")
    public ResponseEntity<ResumenAnaliticaDTO> obtenerResumenTiempoReal() {
        ResumenAnaliticaDTO resumen = new ResumenAnaliticaDTO(
                analiticaStreams.getPagosAprobados().get(),
                analiticaStreams.getPagosRechazados().get(),
                analiticaStreams.getMontoTotalVentas().sum(),
                analiticaStreams.getEventosAuditoria().get(),
                analiticaStreams.getUltimosEventos()
        );
        return ResponseEntity.ok(resumen);
    }
}