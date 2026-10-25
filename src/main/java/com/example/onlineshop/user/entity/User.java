package com.example.onlineshop.user.entity;

import com.example.onlineshop.cart.entity.*;
import com.example.onlineshop.content.entity.*;
import com.example.onlineshop.order.entity.*;
import com.example.onlineshop.product.entity.*;
import com.example.onlineshop.promotion.entity.*;
import com.example.onlineshop.shipping.entity.*;
import com.example.onlineshop.user.entity.*;
import com.example.onlineshop.wishlist.entity.*;

import jakarta.persistence.*;
        import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email; // Primary Key

    private String firstName;
    private String lastName;
    private String password;
    private String dob;
    private LocalDate lastLoginTime;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "email_verification_expires_at")
    private LocalDateTime emailVerificationTokenExpiresAt;

    @Column(name = "verification_email_sent_at")
    private LocalDateTime verificationEmailSentAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires_at")
    private LocalDateTime passwordResetTokenExpiresAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean deletionRequested = false;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private boolean anonymized = false;

    private LocalDateTime deactivationRequestedAt;
    private LocalDateTime deletionRequestedAt;
    private LocalDateTime anonymizedAt;
    private LocalDateTime retentionDeadline;

    // Implementing UserDetails methods
    @Override
    public String getUsername() {
        return this.email; // Can use email as username
    }

    @Override
    public String getPassword() {
        return this.password; // Password for authentication
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            String roleName = role.getName();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
            if (role.getPermissions() != null) {
                role.getPermissions().forEach(permission ->
                        authorities.add(new SimpleGrantedAuthority(permission.getName())));
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can change this based on your business logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // You can change this based on your business logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can change this based on your business logic
    }

    @Override
    public boolean isEnabled() {
        return this.active && !this.deleted && !this.anonymized && this.emailVerified;
    }

    // Optional: Custom method
    public String getUserId() {
        return this.email;
    }
}
