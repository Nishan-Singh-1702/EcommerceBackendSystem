package com.Ecommerce.service;

import com.Ecommerce.exception.APIException;
import com.Ecommerce.model.Address;
import com.Ecommerce.model.User;
import com.Ecommerce.payload.AddressDTO;
import com.Ecommerce.repository.AddressRepository;
import com.Ecommerce.repository.UserRepository;
import com.Ecommerce.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthUtil authUtil;


    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        Address address = modelMapper.map(addressDTO, Address.class);
        boolean exists = addressRepository.existsByUserAndStreetAndCityAndPincode(user, address.getStreet(), address.getCity(), address.getPincode());
        if (exists) throw new APIException("Address already exists!");
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }
}
