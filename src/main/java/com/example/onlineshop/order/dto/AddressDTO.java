package com.example.onlineshop.order.dto;

import com.example.onlineshop.order.entity.Address;
import com.example.onlineshop.order.entity.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String userId; // To map the associated user

    public AddressDTO() {
    }

    public AddressDTO(Address address) {
        this.id = address.getAddressId();
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.country = address.getCountry();
        this.postalCode = address.getPostalCode();
        this.userId = address.getUser().getEmail();
    }
}
