package cl.duoc.mensajeria_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- USUARIOS ---
    public static final String USUARIOS_EXCHANGE = "usuarios.exchange";
    public static final String USUARIOS_QUEUE = "usuarios.queue";
    public static final String USUARIOS_ROUTING_KEY = "usuarios.routingKey";
    public static final String USUARIOS_DLX = "usuarios.dlx";
    public static final String USUARIOS_DLQ = "usuarios.dlq";
    public static final String USUARIOS_DLQ_ROUTING_KEY = "usuarios.dlq.routingKey";

    @Bean
    public DirectExchange usuariosExchange() { return new DirectExchange(USUARIOS_EXCHANGE); }
    @Bean
    public DirectExchange usuariosDlx() { return new DirectExchange(USUARIOS_DLX); }
    @Bean
    public Queue usuariosDlq() { return QueueBuilder.durable(USUARIOS_DLQ).build(); }
    @Bean
    public Binding usuariosDlqBinding() { return BindingBuilder.bind(usuariosDlq()).to(usuariosDlx()).with(USUARIOS_DLQ_ROUTING_KEY); }
    @Bean
    public Queue usuariosQueue() {
        return QueueBuilder.durable(USUARIOS_QUEUE)
                .withArgument("x-dead-letter-exchange", USUARIOS_DLX)
                .withArgument("x-dead-letter-routing-key", USUARIOS_DLQ_ROUTING_KEY)
                .build();
    }
    @Bean
    public Binding usuariosBinding() { return BindingBuilder.bind(usuariosQueue()).to(usuariosExchange()).with(USUARIOS_ROUTING_KEY); }

    // --- PRODUCTOS ---
    public static final String PRODUCTOS_EXCHANGE = "productos.exchange";
    public static final String PRODUCTOS_QUEUE = "productos.queue";
    public static final String PRODUCTOS_ROUTING_KEY = "productos.routingKey";
    public static final String PRODUCTOS_DLX = "productos.dlx";
    public static final String PRODUCTOS_DLQ = "productos.dlq";
    public static final String PRODUCTOS_DLQ_ROUTING_KEY = "productos.dlq.routingKey";

    @Bean
    public DirectExchange productosExchange() { return new DirectExchange(PRODUCTOS_EXCHANGE); }
    @Bean
    public DirectExchange productosDlx() { return new DirectExchange(PRODUCTOS_DLX); }
    @Bean
    public Queue productosDlq() { return QueueBuilder.durable(PRODUCTOS_DLQ).build(); }
    @Bean
    public Binding productosDlqBinding() { return BindingBuilder.bind(productosDlq()).to(productosDlx()).with(PRODUCTOS_DLQ_ROUTING_KEY); }
    @Bean
    public Queue productosQueue() {
        return QueueBuilder.durable(PRODUCTOS_QUEUE)
                .withArgument("x-dead-letter-exchange", PRODUCTOS_DLX)
                .withArgument("x-dead-letter-routing-key", PRODUCTOS_DLQ_ROUTING_KEY)
                .build();
    }
    @Bean
    public Binding productosBinding() { return BindingBuilder.bind(productosQueue()).to(productosExchange()).with(PRODUCTOS_ROUTING_KEY); }

    // --- PEDIDOS ---
    public static final String PEDIDOS_EXCHANGE = "pedidos.exchange";
    public static final String PEDIDOS_QUEUE = "pedidos.queue";
    public static final String PEDIDOS_ROUTING_KEY = "pedidos.routingKey";
    public static final String PEDIDOS_DLX = "pedidos.dlx";
    public static final String PEDIDOS_DLQ = "pedidos.dlq";
    public static final String PEDIDOS_DLQ_ROUTING_KEY = "pedidos.dlq.routingKey";

    @Bean
    public DirectExchange pedidosExchange() { return new DirectExchange(PEDIDOS_EXCHANGE); }
    @Bean
    public DirectExchange pedidosDlx() { return new DirectExchange(PEDIDOS_DLX); }
    @Bean
    public Queue pedidosDlq() { return QueueBuilder.durable(PEDIDOS_DLQ).build(); }
    @Bean
    public Binding pedidosDlqBinding() { return BindingBuilder.bind(pedidosDlq()).to(pedidosDlx()).with(PEDIDOS_DLQ_ROUTING_KEY); }
    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(PEDIDOS_QUEUE)
                .withArgument("x-dead-letter-exchange", PEDIDOS_DLX)
                .withArgument("x-dead-letter-routing-key", PEDIDOS_DLQ_ROUTING_KEY)
                .build();
    }
    @Bean
    public Binding pedidosBinding() { return BindingBuilder.bind(pedidosQueue()).to(pedidosExchange()).with(PEDIDOS_ROUTING_KEY); }

    // --- PAGOS ---
    public static final String PAGOS_EXCHANGE = "pagos.exchange";
    public static final String PAGOS_QUEUE = "pagos.queue";
    public static final String PAGOS_ROUTING_KEY = "pago.procesado";
    public static final String PAGOS_DLX = "pagos.dlx";
    public static final String PAGOS_DLQ = "pagos.dlq";
    public static final String PAGOS_DLQ_ROUTING_KEY = "pago.dead";

    @Bean
    public DirectExchange pagosExchange() { return new DirectExchange(PAGOS_EXCHANGE); }
    @Bean
    public DirectExchange pagosDlx() { return new DirectExchange(PAGOS_DLX); }
    @Bean
    public Queue pagosDlq() { return QueueBuilder.durable(PAGOS_DLQ).build(); }
    @Bean
    public Binding pagosDlqBinding() { return BindingBuilder.bind(pagosDlq()).to(pagosDlx()).with(PAGOS_DLQ_ROUTING_KEY); }
    @Bean
    public Queue pagosQueue() {
        return QueueBuilder.durable(PAGOS_QUEUE)
                .withArgument("x-dead-letter-exchange", PAGOS_DLX)
                .withArgument("x-dead-letter-routing-key", PAGOS_DLQ_ROUTING_KEY)
                .build();
    }
    @Bean
    public Binding pagosBinding() { return BindingBuilder.bind(pagosQueue()).to(pagosExchange()).with(PAGOS_ROUTING_KEY); }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
}