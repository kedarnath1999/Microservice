package com.techi.microservices.order.service;

import com.techi.microservices.order.client.InventoryClient;
import com.techi.microservices.order.dto.OrderRequest;
import com.techi.microservices.order.event.OrderPlacedEvent;
import com.techi.microservices.order.model.Order;
import com.techi.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public void placeOrder(OrderRequest orderRequest) {
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isProductInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setQuantity(orderRequest.quantity());
            order.setSkuCode(orderRequest.skuCode());
            orderRepository.save(order);

            //Send the messages to kafka topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            orderPlacedEvent.setFirstName((orderRequest.userDetails().firstName()));
            orderPlacedEvent.setLastName((orderRequest.userDetails().lastName()));

            log.info("start sending OderPlaced event to kafka order-placed",orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End sending OderPlaced event to kafka order-placed");

        }
        else{
            throw new RuntimeException("Product with skuCode"+orderRequest.skuCode()+"is not in stock");
        }

    }
}
