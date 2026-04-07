package com.Ecommerce.controller;

import com.Ecommerce.payload.AddressDTO;
import com.Ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AddressService addressService;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> createAddress( @Valid @RequestBody AddressDTO addressDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(addressDTO));
    }
}
