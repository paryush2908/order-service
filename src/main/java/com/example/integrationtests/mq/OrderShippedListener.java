package com.example.integrationtests.mq;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.example.integrationtests.db.OrderRepository;
import com.example.integrationtests.model.Order;
import com.example.integrationtests.model.OrderShippedMessage;
import com.example.integrationtests.model.OrderStatus;

@Component
public class OrderShippedListener {

  private final OrderRepository orderRepository;

  public OrderShippedListener(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  // TODO Do we need this
  @Bean
  public Jackson2JsonMessageConverter converter() {
    return new Jackson2JsonMessageConverter();
  }

  @RabbitListener(queues = "order.shipped.queue")
  public void handleOrderShippedMessage(OrderShippedMessage message) {
    System.out.println("Received Order Shipped Message: " + message);

    Optional<Order> optionalOrder = orderRepository.findById(message.getOrderId());
    if (optionalOrder.isEmpty()) {
      System.out.println("Order Not found {orderId}: " + message.getOrderId());
      return;
    }
    Order order = optionalOrder.get();
    order.setStatus(OrderStatus.SHIPPED);
    order.setTrackingNumber(message.getTrackingNumber());

    orderRepository.save(order);
  }
}