package cl.duoc.producto_service.config;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Inyectamos los valores del application.yml (centralizado)
    @Value("${rabbitmq.exchanges.principal}")
    private String exchangePrincipal;

    @Value("${rabbitmq.queues.notificaciones}")
    private String queueNotificaciones;

    @Value("${rabbitmq.queues.tickets}")
    private String queueTickets;

    @Value("${rabbitmq.queues.dlq}")
    private String queueDlq;

    @Value("${rabbitmq.routing-keys.notificaciones}")
    private String routingNotificaciones;

    @Value("${rabbitmq.routing-keys.tickets}")
    private String routingTickets;

    @Value("${rabbitmq.routing-keys.dlq}")
    private String routingDlq;

    // 1. Declaración del Exchange principal
    @Bean
    public DirectExchange principalExchange() {
        return new DirectExchange(exchangePrincipal);
    }

    // 2. Declaración de la Dead Letter Queue (DLQ) para los mensajes que fallen
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(queueDlq).build();
    }

    @Bean
    public Binding dlqBinding(Queue dlqQueue, DirectExchange principalExchange) {
        return BindingBuilder.bind(dlqQueue).to(principalExchange).with(routingDlq);
    }

    // 3. Cola de Notificaciones (Conectada a la DLQ si algo falla)
    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(queueNotificaciones)
                .withArgument("x-dead-letter-exchange", exchangePrincipal)
                .withArgument("x-dead-letter-routing-key", routingDlq)
                .build();
    }

    @Bean
    public Binding notificacionesBinding(Queue notificacionesQueue, DirectExchange principalExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(principalExchange).with(routingNotificaciones);
    }

    // 4. Cola de Tickets (Conectada también a la DLQ)
    @Bean
    public Queue ticketsQueue() {
        return QueueBuilder.durable(queueTickets)
                .withArgument("x-dead-letter-exchange", exchangePrincipal)
                .withArgument("x-dead-letter-routing-key", routingDlq)
                .build();
    }

    @Bean
    public Binding ticketsBinding(Queue ticketsQueue, DirectExchange principalExchange) {
        return BindingBuilder.bind(ticketsQueue).to(principalExchange).with(routingTickets);
    }
}