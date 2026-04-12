package com.Ecommerce.service;

import com.Ecommerce.exception.APIException;
import com.Ecommerce.exception.ResourceNotFoundException;
import com.Ecommerce.model.Cart;
import com.Ecommerce.model.CartItem;
import com.Ecommerce.model.Product;
import com.Ecommerce.payload.CartDTO;
import com.Ecommerce.payload.CartResponse;
import com.Ecommerce.payload.ProductDTO;
import com.Ecommerce.repository.CartItemRepository;
import com.Ecommerce.repository.CartRepository;
import com.Ecommerce.repository.ProductRepository;
import com.Ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AuthUtil authUtil;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    CartRepository cartRepository;


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Product productFromDb = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        Cart cart = createCart();
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);
        if(cartItem!=null) throw new APIException("Product "+productFromDb.getProductName()+" is already exist in cart !!");
        if(productFromDb.getQuantity()==0)throw new APIException(productFromDb.getProductName()+" is not available !!");
        if(productFromDb.getQuantity()<quantity)throw new APIException("Please make order of the "+productFromDb.getProductName()+" less than or equal to the quantity: "+productFromDb.getQuantity());

        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(productFromDb);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(productFromDb.getDiscount());
        newCartItem.setProductPrice(productFromDb.getSpecialPrice());

        cartItemRepository.save(newCartItem);

//        productFromDb.setQuantity(productFromDb.getQuantity()); // if you want to reduce product quantity once it is added in cart then complete this

        cart.setTotalPrice(cart.getTotalPrice() + (productFromDb.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item->{
            ProductDTO productDTO = modelMapper.map(item.getProduct(),ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Override
    public CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        if (cartPage.isEmpty()) throw new APIException("No Cart Created till now !!");

        List<CartDTO> cartDTOS = cartPage.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOS = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO dto = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                dto.setQuantity(cartItem.getQuantity());
                return dto;
            }).toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();

        CartResponse response = new CartResponse();
        response.setContent(cartDTOS);
        response.setPageNumber(pageable.getPageNumber());
        response.setPageSize(cartPage.getSize());
        response.setTotalElement(cartPage.getTotalElements());
        response.setTotalPage(cartPage.getTotalPages());
        response.setLastPage(cartPage.isLast());
        return response;
    }

    @Override
    public CartDTO getCart() {
        String email = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(email);
        if(cart==null) throw new APIException("No Cart found !!");
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(product->modelMapper.map(product.getProduct(),ProductDTO.class)).toList();
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String email = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(email);
        if(cart==null)throw new APIException("No cart found !!");
        Long cartId = cart.getCartId();
        Product product = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null)throw new APIException("Product: " + product.getProductName() + " is not available in the cart");
        int newQuantity = cartItem.getQuantity() + quantity;
        if (newQuantity < 0)throw new APIException("The resulting quantity cannot be negative");
        if (newQuantity > product.getQuantity())throw new APIException("Only " + product.getQuantity() + " items available for " + product.getProductName());
        if (newQuantity == 0) {
            cart.setTotalPrice(cart.getTotalPrice() -(cartItem.getProductPrice() * cartItem.getQuantity()));
            cart.getCartItems().remove(cartItem);
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream().map(item -> {ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
                dto.setQuantity(item.getQuantity());
                return dto;
            }).toList();
            cartDTO.setProducts(products);
            return cartDTO;
        }
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartItem.setQuantity(newQuantity);
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream().map(item -> {
            ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
            dto.setQuantity(item.getQuantity());
            return dto;
        }).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Long productId) {
        String email = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(email);
        if(cart==null) throw new APIException("No cart found !!");
        Long cartId = cart.getCartId();
        CartItem cartItemFromDb = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItemFromDb==null)throw new ResourceNotFoundException("Product","productId",productId);
        cart.setTotalPrice(cart.getTotalPrice()-(cartItemFromDb.getProductPrice()*cartItemFromDb.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(productId,cartId);
        return "Product "+cartItemFromDb.getProduct().getProductName()+" is removed from Cart !!";
    }

    public Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }
}
