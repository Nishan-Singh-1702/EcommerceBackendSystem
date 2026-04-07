package com.Ecommerce.repository;

import com.Ecommerce.model.Address;
import com.Ecommerce.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByUserEmail(String s);

    boolean existsByUserAndStreetAndCityAndPincode(User user, @NotBlank @Size(min = 5, message = "Street name must contain atleast 5 characters") String street, @NotBlank @Size(min = 3, message = "City name must contain atleast 3 characters") String city, @NotBlank @Size(min = 6, message = "PinCode name must contain atleast 6 characters") String pincode);
}
