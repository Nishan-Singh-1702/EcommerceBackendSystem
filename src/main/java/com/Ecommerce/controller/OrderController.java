package com.Ecommerce.controller;

import com.Ecommerce.payload.OrderDTO;
import com.Ecommerce.payload.OrderRequestDTO;
import com.Ecommerce.service.OrderService;
import com.Ecommerce.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @Valid @RequestBody OrderRequestDTO orderRequestDTO){
        String email = authUtil.loggedInEmail();
        OrderDTO order = orderService.placedOrder(email,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

}
