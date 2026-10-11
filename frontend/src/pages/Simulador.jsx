import React, { useState } from 'react';

export default function Simulador() {
  const [email, setEmail] = useState('offgdev@gmail.com');
  const [logs, setLogs] = useState([]);

  const disparar = async (tipo) => {
    try {
      const response = await fetch('/api/pagos/simular/evento', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ tipo, email })
      });

      const data = await response.json();
      const logEntrada = `[${new Date().toLocaleTimeString()}] [${data.estado || 'OK'}] ${data.mensaje || 'Evento procesado'}`;
      setLogs(prev => [logEntrada, ...prev]);
    } catch (err) {
      const logError = `[${new Date().toLocaleTimeString()}] [ERROR] ${err.message}`;
      setLogs(prev => [logError, ...prev]);
    }
  };

  return (
    <div style={{ maxWidth: '750px', margin: '40px auto', padding: '30px', fontFamily: 'system-ui, sans-serif', backgroundColor: '#1e1e2e', color: '#cdd6f4', borderRadius: '12px', boxShadow: '0 8px 24px rgba(0,0,0,0.3)' }}>
      <h2 style={{ margin: '0 0 10px 0', color: '#89b4fa' }}>⚡ Panel de Simulación EDA - RabbitMQ</h2>
      <p style={{ color: '#a6adc8', fontSize: '14px' }}>Dispara eventos en tiempo real hacia las colas de microservicios para demostración.</p>
      
      <div style={{ margin: '20px 0' }}>
        <label style={{ display: 'block', marginBottom: '8px', fontSize: '14px', fontWeight: 'bold' }}>Correo de Destino para Notificaciones:</label>
        <input 
          type="email" 
          value={email} 
          onChange={(e) => setEmail(e.target.value)} 
          style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #45475a', backgroundColor: '#313244', color: '#fff', fontSize: '15px' }}
        />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', margin: '25px 0' }}>
        <button 
          onClick={() => disparar('PAGO_APROBADO')}
          style={{ padding: '14px', backgroundColor: '#a6e3a1', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px' }}>
          🛒 Simular Pago Aprobado (pagos.queue)
        </button>

        <button 
          onClick={() => disparar('USUARIO_CREADO')}
          style={{ padding: '14px', backgroundColor: '#89b4fa', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px' }}>
          👤 Simular Bienvenida Usuario (usuarios.queue)
        </button>

        <button 
          onClick={() => disparar('PAGO_RECHAZADO')}
          style={{ padding: '14px', backgroundColor: '#f38ba8', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px', gridColumn: 'span 2' }}>
          ⚠️ Simular Alerta de Pago Rechazado (pagos.dlq)
        </button>
      </div>

      <h3 style={{ fontSize: '16px', borderBottom: '1px solid #45475a', paddingBottom: '8px', marginTop: '30px' }}>📋 Console Log de Salida:</h3>
      <div style={{ backgroundColor: '#11111b', padding: '15px', borderRadius: '8px', height: '180px', overflowY: 'auto', fontFamily: 'monospace', fontSize: '13px', color: '#a6e3a1' }}>
        {logs.length === 0 ? <span style={{ color: '#585b70' }}>Esperando interacción...</span> : logs.map((log, index) => (
          <div key={index} style={{ marginBottom: '6px' }}>{log}</div>
        ))}
      </div>
    </div>
  );
}