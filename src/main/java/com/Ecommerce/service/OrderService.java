package com.Ecommerce.service;

import com.Ecommerce.payload.OrderDTO;
import com.Ecommerce.payload.OrderRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderService {
    @Transactional
    OrderDTO placedOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
