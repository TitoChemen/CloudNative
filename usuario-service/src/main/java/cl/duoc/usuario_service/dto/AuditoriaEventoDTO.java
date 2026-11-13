package cl.duoc.usuario_service.dto;

import java.io.Serializable;

public class AuditoriaEventoDTO implements Serializable {
    private String auditId;
    private String servicio;
    private String accion;
    private String usuarioEmail;
    private String timestamp;

    public AuditoriaEventoDTO() {}

    public AuditoriaEventoDTO(String auditId, String servicio, String accion, String usuarioEmail, String timestamp) {
        this.auditId = auditId;
        this.servicio = servicio;
        this.accion = accion;
        this.usuarioEmail = usuarioEmail;
        this.timestamp = timestamp;
    }

    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }
    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}