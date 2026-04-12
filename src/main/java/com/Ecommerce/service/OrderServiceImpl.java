package com.Ecommerce.service;

import com.Ecommerce.exception.APIException;
import com.Ecommerce.exception.ResourceNotFoundException;
import com.Ecommerce.model.*;
import com.Ecommerce.payload.OrderDTO;
import com.Ecommerce.payload.OrderItemDTO;
import com.Ecommerce.payload.OrderRequestDTO;
import com.Ecommerce.repository.*;
import com.Ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    AuthUtil authUtil;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartService cartService;
    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placedOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
//        Getting User cart
        Cart cart = cartRepository.findCartByEmail(email);
        if(cart==null) throw new ResourceNotFoundException("Cart","email",email);
        Address address = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));

//        Create a new order with payment info
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setEmail(email);
        order.setTotalAmount(cart.getTotalPrice());
        order.setAddress(address);
        order.setOrderStatus("Order Accepted !!");

        Payment payment = new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
        payment.setOrder(order);
        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);
        Order savedOrder = orderRepository.save(order);

//        Get items from the cart into the order items
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty())throw new APIException("Cart is empty !!");

        List<OrderItem>orderItems = new ArrayList<>();
        for(CartItem cartItem: cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        orderItems=orderItemRepository.saveAll(orderItems);

//        Update product stock
//        Step 1: Collect product IDs before modifying the collection

        List<Long> productIds = cart.getCartItems().stream()
                .map(item -> item.getProduct().getProductId())
                .toList();

//        Step 2: Update stock
        cart.getCartItems().forEach(item -> {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        });

//      Step 3: Clear the cart after iteration is complete
        productIds.forEach(cartService::deleteProductFromCart);

//        Send back the order Summary
        OrderDTO orderDTO = modelMapper.map(savedOrder,OrderDTO.class);
        orderItems.forEach(item->orderDTO.getOrderItem().add(modelMapper.map(item, OrderItemDTO.class)));
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
