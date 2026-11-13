package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.exception.ErrorNoRecuperableException;
import cl.duoc.mensajeria_service.exception.ErrorRecuperableException;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Centraliza la confirmación manual (ACK/NACK) de los consumidores.
 *
 * <ul>
 *   <li>Éxito                      -> basicAck.</li>
 *   <li>ErrorNoRecuperable / otro  -> basicNack(requeue=false): va a la DLQ y se registra en log.</li>
 *   <li>ErrorRecuperable           -> se reintenta con backoff; si se agotan los intentos
 *       -> basicNack(requeue=false): va a la DLQ y se registra en log.</li>
 * </ul>
 *
 * El NACK sin requeue activa el x-dead-letter-exchange configurado en cada cola principal.
 */
@Slf4j
@Component
public class AckHandler {

    @FunctionalInterface
    public interface AccionMensaje {
        void ejecutar(String mensaje) throws Exception;
    }

    private final int maxIntentos;
    private final long intervaloInicialMs;
    private final double multiplicador;

    public AckHandler(@Value("${rabbitmq.retry.max-attempts:3}") int maxIntentos,
                      @Value("${rabbitmq.retry.initial-interval-ms:2000}") long intervaloInicialMs,
                      @Value("${rabbitmq.retry.multiplier:2.0}") double multiplicador) {
        this.maxIntentos = Math.max(1, maxIntentos);
        this.intervaloInicialMs = Math.max(0, intervaloInicialMs);
        this.multiplicador = multiplicador < 1 ? 1 : multiplicador;
    }

    public void procesar(String cola, String mensaje, Channel channel, long deliveryTag,
                         AccionMensaje accion) throws IOException {
        long espera = intervaloInicialMs;

        for (int intento = 1; ; intento++) {
            try {
                accion.ejecutar(mensaje);
            } catch (ErrorNoRecuperableException e) {
                rechazar(cola, mensaje, channel, deliveryTag, "error no recuperable: " + e.getMessage(), e);
                return;
            } catch (ErrorRecuperableException e) {
                if (intento >= maxIntentos) {
                    rechazar(cola, mensaje, channel, deliveryTag,
                            "reintentos agotados (" + maxIntentos + "): " + e.getMessage(), e);
                    return;
                }
                log.warn("[RETRY] cola={} intento={}/{} error='{}'. Reintentando en {} ms",
                        cola, intento, maxIntentos, e.getMessage(), espera);
                if (!esperar(espera)) {
                    // El hilo fue interrumpido (apagado): se devuelve el mensaje a la cola, sin perderlo.
                    channel.basicNack(deliveryTag, false, true);
                    log.warn("[REQUEUE] cola={} interrumpido durante el reintento; mensaje devuelto a la cola", cola);
                    return;
                }
                espera = Math.round(espera * multiplicador);
                continue;
            } catch (Exception e) {
                rechazar(cola, mensaje, channel, deliveryTag, "error inesperado: " + e.getMessage(), e);
                return;
            }

            // Procesado correctamente: confirmación explícita (fuera del try para no confundir fallos de red con fallos de negocio).
            channel.basicAck(deliveryTag, false);
            log.info("[ACK] cola={} intento={} mensaje='{}'", cola, intento, mensaje);
            return;
        }
    }

    private void rechazar(String cola, String mensaje, Channel channel, long deliveryTag,
                          String motivo, Exception causa) throws IOException {
        log.error("[DLQ] Mensaje no entregado. cola={} motivo={} mensaje='{}'", cola, motivo, mensaje, causa);
        channel.basicNack(deliveryTag, false, false);
    }

    private boolean esperar(long ms) {
        try {
            Thread.sleep(ms);
            return true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
