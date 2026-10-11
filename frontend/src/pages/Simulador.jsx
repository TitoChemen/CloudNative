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
      const badge = data.estado === 'OK' ? '✅ OK' : '⚠️ DLQ';
      const logEntrada = `[${new Date().toLocaleTimeString()}] [${badge}] ${data.mensaje || 'Evento procesado'}`;
      setLogs(prev => [logEntrada, ...prev]);
    } catch (err) {
      const logError = `[${new Date().toLocaleTimeString()}] [❌ ERROR] ${err.message}`;
      setLogs(prev => [logError, ...prev]);
    }
  };

  return (
    <div style={{ maxWidth: '850px', margin: '40px auto', padding: '30px', fontFamily: 'system-ui, sans-serif', backgroundColor: '#1e1e2e', color: '#cdd6f4', borderRadius: '12px', boxShadow: '0 8px 24px rgba(0,0,0,0.3)' }}>
      <h2 style={{ margin: '0 0 10px 0', color: '#89b4fa', display: 'flex', alignItems: 'center', gap: '10px' }}>
        ⚡ Panel de Simulación EDA - RabbitMQ (8 Colas)
      </h2>
      <p style={{ color: '#a6adc8', fontSize: '14px' }}>Dispara eventos en tiempo real hacia las colas activas del clúster.</p>
      
      <div style={{ margin: '20px 0' }}>
        <label style={{ display: 'block', marginBottom: '8px', fontSize: '14px', fontWeight: 'bold' }}>Correo de Destino para Notificaciones:</label>
        <input 
          type="email" 
          value={email} 
          onChange={(e) => setEmail(e.target.value)} 
          style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #45475a', backgroundColor: '#313244', color: '#fff', fontSize: '15px' }}
        />
      </div>

      <h3 style={{ fontSize: '15px', color: '#a6e3a1', borderBottom: '1px solid #45475a', paddingBottom: '6px', marginTop: '25px' }}>
        🟢 Colas Principales (Eventos Exitosos):
      </h3>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', margin: '15px 0' }}>
        <button onClick={() => disparar('PAGO_APROBADO')} style={{ padding: '12px', backgroundColor: '#a6e3a1', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' }}>
          🛒 Pago Aprobado (pagos.queue)
        </button>
        <button onClick={() => disparar('USUARIO_CREADO')} style={{ padding: '12px', backgroundColor: '#89b4fa', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' }}>
          👤 Usuario Creado (usuarios.queue)
        </button>
        <button onClick={() => disparar('PEDIDO_DESPACHADO')} style={{ padding: '12px', backgroundColor: '#94e2d5', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' }}>
          📦 Pedido Despachado (pedidos.queue)
        </button>
        <button onClick={() => disparar('STOCK_ACTUALIZADO')} style={{ padding: '12px', backgroundColor: '#f9e2af', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' }}>
          🏷️ Stock Actualizado (productos.queue)
        </button>
      </div>

      <h3 style={{ fontSize: '15px', color: '#f38ba8', borderBottom: '1px solid #45475a', paddingBottom: '6px', marginTop: '25px' }}>
        🔴 Dead Letter Queues (Alertas de Fallo & Manejo de Errores):
      </h3>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', margin: '15px 0' }}>
        <button onClick={() => disparar('PAGO_RECHAZADO')} style={{ padding: '12px', backgroundColor: '#f38ba8', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' }}>
          ⚠️ Pago Rechazado (pagos.dlq)
        </button>
        <button onClick={() => disparar('USUARIO_ERROR')} style={{ padding: '12px', backgroundColor: '#fab387', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' }}>
          ⚠️ Error Usuario (usuarios.dlq)
        </button>
        <button onClick={() => disparar('PEDIDO_ERROR')} style={{ padding: '12px', backgroundColor: '#f5e0dc', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' }}>
          ⚠️ Error Pedido (pedidos.dlq)
        </button>
        <button onClick={() => disparar('STOCK_AGOTADO')} style={{ padding: '12px', backgroundColor: '#eba0ac', color: '#11111b', fontWeight: 'bold', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' }}>
          🚨 Stock Agotado (productos.dlq)
        </button>
      </div>

      <h3 style={{ fontSize: '15px', borderBottom: '1px solid #45475a', paddingBottom: '6px', marginTop: '30px', color: '#cdd6f4' }}>
        📋 Historial de Eventos en Tiempo Real:
      </h3>
      <div style={{ backgroundColor: '#11111b', padding: '15px', borderRadius: '8px', height: '180px', overflowY: 'auto', fontFamily: 'monospace', fontSize: '13px', color: '#a6e3a1' }}>
        {logs.length === 0 ? <span style={{ color: '#585b70' }}>Haz clic en un botón para enviar un evento a RabbitMQ...</span> : logs.map((log, index) => (
          <div key={index} style={{ marginBottom: '6px' }}>{log}</div>
        ))}
      </div>
    </div>
  );
}