package com.techi.microservices.order.service;

import com.techi.microservices.order.client.InventoryClient;
import com.techi.microservices.order.dto.OrderRequest;
import com.techi.microservices.order.model.Order;
import com.techi.microservices.order.repository.OrderRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;



    public void placeOrder(OrderRequest orderRequest) {
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isProductInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setQuantity(orderRequest.quantity());
            order.setSkuCode(orderRequest.skuCode());
            orderRepository.save(order);
        }
        else{
            throw new RuntimeException("Product with skuCode"+orderRequest.skuCode()+"is not in stock");
        }

    }
}
