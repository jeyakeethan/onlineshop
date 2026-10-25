package com.example.onlineshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name="addresses")
@Getter
@Setter
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    @Column(nullable = false)
    private boolean defaultAddress = false;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "email")
    private User user;
}
