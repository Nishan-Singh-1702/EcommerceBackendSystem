package com.Ecommerce.controller;

import com.Ecommerce.config.AppConstant;
import com.Ecommerce.payload.AddressDTO;
import com.Ecommerce.payload.AddressResponse;
import com.Ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AddressService addressService;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(addressDTO));
    }

    @GetMapping("/addresses")
    public ResponseEntity<AddressResponse> getAllAddress(@RequestParam(value = "pageNumber",required = false,defaultValue = AppConstant.pageNumber)Integer pageNumber,
                                                         @RequestParam(value = "pageSize",required = false,defaultValue = AppConstant.pageSize)Integer pageSize,
                                                         @RequestParam(value = "sortBy",required = false,defaultValue = AppConstant.sortBy)String sortBy,
                                                         @RequestParam(value = "sortOrder",required = false, defaultValue = AppConstant.sortDir)String sortOrder){
        return ResponseEntity.ok(addressService.getAllAddress(pageNumber,pageSize,sortOrder,sortBy));
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }

    @GetMapping("/address/user/address")
    public ResponseEntity<List<AddressDTO>> getUsersAddress(){
        return ResponseEntity.ok(addressService.getAddressByUser());
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO){
        return ResponseEntity.ok(addressService.updateAddressById(addressId,addressDTO));
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddressById(Long addressId){
        return ResponseEntity.ok(addressService.deleteAddressById(addressId));
    }


}
