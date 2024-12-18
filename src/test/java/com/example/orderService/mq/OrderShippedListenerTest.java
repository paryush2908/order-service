package com.example.orderService.mq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.orderService.db.OrderRepository;
import com.example.orderService.model.Order;
import com.example.orderService.model.OrderShippedMessage;
import com.example.orderService.model.OrderStatus;

@SpringBootTest
@ActiveProfiles("test")
public class OrderShippedListenerTest {

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  RabbitTemplate rabbitTemplate;

  @Test
  public void testOrderShippedMessageReceived() throws InterruptedException {

    // Given
    Order order = new Order(
        101L,
        "ORD_AAX111",
        OrderStatus.PENDING,
        new BigDecimal("499.99"),
        LocalDateTime.now());
    orderRepository.save(order);

    // When
    sendOrderShippedMessage(order.getId(), "#123"); // send message to queue to test listener behaviour in application
    Thread.sleep(2000); // wait for message to be received by listener

    // Then
    Order shippedOrder = orderRepository.findById(order.getId()).get();

    assertEquals(OrderStatus.SHIPPED, shippedOrder.getStatus());
    assertEquals("#123", shippedOrder.getTrackingNumber());
  }

  private void sendOrderShippedMessage(Long orderId, String trackingNumber) {
    OrderShippedMessage message = new OrderShippedMessage();
    message.setOrderId(orderId);
    message.setTrackingNumber(trackingNumber);
    message.setShippedDate(LocalDateTime.now());

    rabbitTemplate.convertAndSend("order.exchange", "order.shipped", message);
  }
}
