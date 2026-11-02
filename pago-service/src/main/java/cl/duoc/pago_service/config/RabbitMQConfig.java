package cl.duoc.pago_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAGOS_EXCHANGE = "pagos.exchange";
    public static final String PAGOS_QUEUE = "pagos.queue";
    public static final String PAGOS_ROUTING_KEY = "pago.procesado";

    public static final String PAGOS_DLX = "pagos.dlx";
    public static final String PAGOS_DLQ = "pagos.dlq";
    public static final String PAGOS_DLQ_ROUTING_KEY = "pago.dead";

    @Bean
    public DirectExchange pagosExchange() {
        return new DirectExchange(PAGOS_EXCHANGE);
    }

    @Bean
    public DirectExchange pagosDlx() {
        return new DirectExchange(PAGOS_DLX);
    }

    @Bean
    public Queue pagosDlq() {
        return QueueBuilder.durable(PAGOS_DLQ).build();
    }

    @Bean
    public Binding pagosDlqBinding() {
        return BindingBuilder.bind(pagosDlq()).to(pagosDlx()).with(PAGOS_DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue pagosQueue() {
        return QueueBuilder.durable(PAGOS_QUEUE)
                .withArgument("x-dead-letter-exchange", PAGOS_DLX)
                .withArgument("x-dead-letter-routing-key", PAGOS_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding pagosBinding() {
        return BindingBuilder.bind(pagosQueue()).to(pagosExchange()).with(PAGOS_ROUTING_KEY);
    }
}