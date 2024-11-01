package com.example.integrationtests.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.integrationtests.model.Order;
import com.example.integrationtests.model.OrderPlacedMessage;


@Service
public class OrderPlacedSender {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  public void sendOrderPlacedMessage(Order order) {
    OrderPlacedMessage message = new OrderPlacedMessage(order);
    rabbitTemplate.convertAndSend("order.exchange", "order.placed", message);
    System.out.println("Order Placed Message Sent: " + message);
  }
}