package com.Ecommerce.service;

import com.Ecommerce.exception.APIException;
import com.Ecommerce.exception.ResourceNotFoundException;
import com.Ecommerce.model.Address;
import com.Ecommerce.model.User;
import com.Ecommerce.payload.AddressDTO;
import com.Ecommerce.payload.AddressResponse;
import com.Ecommerce.repository.AddressRepository;
import com.Ecommerce.repository.UserRepository;
import com.Ecommerce.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public AddressResponse getAllAddress(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByOrder);
        Page<Address> addressPage = addressRepository.findAll(pageable);
        if(addressPage.isEmpty()) throw new APIException("No address created !!");
        List<AddressDTO> addressDTOS = addressPage.stream().map(address->modelMapper.map(address,AddressDTO.class)).toList();
        AddressResponse response = new AddressResponse();
        response.setContent(addressDTOS);
        response.setPageNumber(addressPage.getNumber());
        response.setPageSize(addressPage.getSize());
        response.setTotalPage(addressPage.getTotalPages());
        response.setTotalElement(addressPage.getTotalElements());
        response.setLastPage(addressPage.isLast());
        return response;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address addressFromDb = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));
        return modelMapper.map(addressFromDb,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUsersAddress() {
        User user = authUtil.loggedInUser();
        List<Address> addresses = addressRepository.findAddressByUser(user);
        if(addresses.isEmpty())throw new APIException("No Address found !!");
        return addresses.stream().map(address->modelMapper.map(address,AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {

        User user = authUtil.loggedInUser();
        Address addressFromDb = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));
        if(addressFromDb.getUser() == null || !addressFromDb.getUser().getUserId().equals(user.getUserId())){
            throw new APIException("Access Denied: you are not allowed to update this address");
        }
        addressFromDb.setState(addressDTO.getState());
        addressFromDb.setStreet(addressDTO.getStreet());
        addressFromDb.setCity(addressDTO.getCity());
        addressFromDb.setCountry(addressDTO.getCountry());
        addressFromDb.setPincode(addressDTO.getPincode());
        addressFromDb.setBuildingName(addressDTO.getBuildingName());

        Address savedAddress = addressRepository.save(addressFromDb);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        User user = authUtil.loggedInUser();
        Address addressFromDb = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));

        if(addressFromDb.getUser() == null || !addressFromDb.getUser().getUserId().equals(user.getUserId())){
            throw new APIException("You are not allowed to delete this address");
        }
        addressRepository.delete(addressFromDb);
        return "Address deleted successfully !!";
    }
}
