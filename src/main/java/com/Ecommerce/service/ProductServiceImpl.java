package com.Ecommerce.service;

import com.Ecommerce.exception.APIException;
import com.Ecommerce.exception.ResourceNotFoundException;
import com.Ecommerce.model.Category;
import com.Ecommerce.model.Product;
import com.Ecommerce.payload.ProductDTO;
import com.Ecommerce.payload.ProductResponse;
import com.Ecommerce.repository.CategoryRepository;
import com.Ecommerce.repository.ProductRepository;
import com.Ecommerce.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    FileService fileService;

    @Autowired
    AuthUtil authUtil;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO createProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
        Product productFromDb = productRepository.findByProductName(product.getProductName());
        if(productFromDb!=null)throw new APIException("Product with productName: "+product.getProductName()+" already exist !!");
        product.setCategory(category);
        product.setImage("Product.Jpg");
        Double specialPrice = product.getPrice()-(product.getPrice()*(product.getDiscount()/100.0));
        product.setSpecialPrice(specialPrice);
        product.setUser(authUtil.loggedInUser());
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()) throw new APIException("No Product is created till now !!");
        List<ProductDTO> productDTOS = products.stream()
                .map(product->modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse response = new ProductResponse();
        response.setContent(productDTOS);
        response.setPageNumber(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalProduct(productPage.getTotalElements());
        response.setTotalPage(productPage.getTotalPages());
        response.setLastPage(productPage.isLast());
        return response;
    }

    @Override
    public ProductResponse getProductsByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Long categoryId) {
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByOrder);
        Page<Product> productPage = productRepository.findByCategoryCategoryIdOrderByPriceAsc(pageDetails,categoryId);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()) throw new ResourceNotFoundException("Products","categoryId",categoryId);
        List<ProductDTO> productDTOS = products.stream()
                .map(product->modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse response = new ProductResponse();
        response.setContent(productDTOS);
        response.setPageNumber(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalProduct(productPage.getTotalElements());
        response.setTotalPage(productPage.getTotalPages());
        response.setLastPage(productPage.isLast());
        return response;
    }

    @Override
    public ProductResponse getProductByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,String keyword) {
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetail = PageRequest.of(pageNumber,pageSize,sortByOrder);
        Page<Product> productPage = productRepository.findByProductNameContainingIgnoreCase(pageDetail,keyword);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()) throw new ResourceNotFoundException("Product","Keyword:",keyword);
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse response = new ProductResponse();
        response.setContent(productDTOS);
        response.setPageNumber(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalProduct(productPage.getTotalElements());
        response.setTotalPage(productPage.getTotalPages());
        response.setLastPage(productPage.isLast());
        return response;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDb = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        productFromDb.setProductName(productDTO.getProductName());
        productFromDb.setDiscount(productDTO.getDiscount());
        productFromDb.setPrice(productDTO.getPrice());
        Double specialPrice = productDTO.getPrice()-(productDTO.getPrice()*(productDTO.getDiscount()/100.0));
        productFromDb.setSpecialPrice(specialPrice);
        productFromDb.setQuantity(productDTO.getQuantity());
        productFromDb.setDescription(productDTO.getDescription());
        Product savedProduct = productRepository.save(productFromDb);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDb = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        productRepository.delete(productFromDb);
        return modelMapper.map(productFromDb,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDb = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));

        String fileName = fileService.uploadImage(path, image);

        productFromDb.setImage(fileName);
        Product updatedProduct = productRepository.save(productFromDb);
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }


}
