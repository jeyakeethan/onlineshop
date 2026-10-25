package com.example.onlineshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bannerId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String mediaUrl;

    private String linkUrl;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    @Column(nullable = false)
    private boolean archived;
}