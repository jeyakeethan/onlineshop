package com.example.onlineshop.user.service;

import com.example.onlineshop.user.dto.UserDTO;
import com.example.onlineshop.user.dto.RegistrationRequest;
import com.example.onlineshop.user.entity.Role;
import com.example.onlineshop.user.entity.TokenSession;
import com.example.onlineshop.user.entity.User;
import com.example.onlineshop.user.repository.RoleRepository;
import com.example.onlineshop.user.repository.TokenSessionRepository;
import com.example.onlineshop.user.repository.UserRepository;
import com.example.onlineshop.security.JwtUtil;
import com.example.onlineshop.common.security.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenSessionRepository tokenSessionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private EmailService emailService;

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must contain at least 8 characters, including uppercase, lowercase, a number, and a special character.");
        }
    }

    public String registerUser(RegistrationRequest request) {
        return registerUser(request, List.of("BUYER"));
    }

    public String registerUser(UserDTO userDTO) {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail(userDTO.getEmail());
        request.setFirstName(userDTO.getFirstName());
        request.setLastName(userDTO.getLastName());
        request.setPassword(userDTO.getPassword());
        request.setDob(userDTO.getDob());
        List<String> roles = userDTO.getRoles() == null || userDTO.getRoles().isEmpty()
                ? List.of("BUYER")
                : userDTO.getRoles();
        return registerUser(request, roles);
    }

    private String registerUser(RegistrationRequest request, List<String> requestedRoles) {
        if (request == null) {
            throw new IllegalArgumentException("User details are required.");
        }

        String normalizedEmail = normalizeEmail(request.getEmail());
        if (normalizedEmail == null || normalizedEmail.isBlank() || !normalizedEmail.contains("@")) {
            throw new IllegalArgumentException("A valid email is required.");
        }

        if (userRepository.existsById(normalizedEmail)) {
            throw new IllegalArgumentException("This email is already registered.");
        }

        validatePassword(request.getPassword());

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));

        List<Role> userRoles = requestedRoles.stream()
            .map(roleName -> roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
            .collect(Collectors.toList());
        user.setRoles(userRoles);
        userRepository.save(user);
        emailService.sendVerificationEmail(normalizedEmail, user.getEmailVerificationToken());

        return "Registration successful. Please verify your email before you can sign in with a password.";
    }

    public String authenticateUser(String userId, String password) {
        String normalizedUserId = normalizeEmail(userId);
        if (normalizedUserId == null || normalizedUserId.isBlank() || password == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findById(normalizedUserId).orElse(null);

        if (user == null || !user.isEmailVerified() || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        user.setLastLoginTime(LocalDate.now());
        userRepository.save(user);

        return issueToken(user);
    }

    public String renewToken(String userId) {
        String normalizedUserId = normalizeEmail(userId);
        User user = userRepository.findById(normalizedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isEmailVerified()) {
            throw new IllegalStateException("Email not verified. Please verify your email before refreshing tokens.");
        }
        return issueToken(user);
    }

    private String issueToken(User user) {
        String token = jwtUtil.generateToken(user);
        TokenSession session = new TokenSession();
        session.setTokenId(jwtUtil.extractTokenId(token));
        session.setUserEmail(user.getUsername());
        session.setIssuedAt(jwtUtil.extractClaim(token, claims -> claims.getIssuedAt().toInstant()));
        session.setExpiresAt(jwtUtil.extractExpiration(token).toInstant());
        tokenSessionRepository.save(session);
        return token;
    }

    public boolean isTokenActive(String token, String userEmail) {
        String tokenId = jwtUtil.extractTokenId(token);
        return tokenId != null && tokenSessionRepository
                .existsByTokenIdAndUserEmailAndRevokedAtIsNullAndExpiresAtAfter(tokenId, userEmail, Instant.now());
    }

    public void revokeToken(String token) {
        String tokenId = jwtUtil.extractTokenId(token);
        String userEmail = jwtUtil.extractUsername(token);
        TokenSession session = tokenSessionRepository
                .findByTokenIdAndUserEmailAndRevokedAtIsNull(tokenId, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token session is not active."));
        session.setRevokedAt(Instant.now());
        tokenSessionRepository.save(session);
    }

    @Transactional
    public String revokeAllSessionsForCurrentUser() {
        String userEmail = authenticationHelper.getCurrentUserId();
        List<TokenSession> activeSessions = tokenSessionRepository.findByUserEmailAndRevokedAtIsNull(userEmail);
        if (activeSessions == null || activeSessions.isEmpty()) {
            return "No active sessions were found for this account.";
        }

        Instant now = Instant.now();
        for (TokenSession session : activeSessions) {
            session.setRevokedAt(now);
            tokenSessionRepository.save(session);
        }
        return "All active sessions for this account have been revoked.";
    }

    public String verifyEmail(String email, String token) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findById(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmailVerificationToken() == null || !user.getEmailVerificationToken().equals(token)) {
            throw new IllegalArgumentException("Invalid or expired email verification token.");
        }

        if (user.getEmailVerificationTokenExpiresAt() != null && user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Email verification token has expired.");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        user.setVerificationEmailSentAt(null);
        userRepository.save(user);
        return "Email verified successfully.";
    }

    public String resendVerificationEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            return "If the account exists and is not yet verified, a new verification email has been queued.";
        }

        User user = userRepository.findById(normalizedEmail).orElse(null);
        if (user == null || user.isEmailVerified()) {
            return "If the account exists and is not yet verified, a new verification email has been queued.";
        }

        if (user.getVerificationEmailSentAt() != null && user.getVerificationEmailSentAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
            return "If the account exists and is not yet verified, a new verification email has been queued.";
        }

        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        user.setVerificationEmailSentAt(LocalDateTime.now());
        userRepository.save(user);
        try {
            emailService.sendVerificationEmail(normalizedEmail, token);
        } catch (Exception ignored) {
            // Intentionally swallow mail failures for now; the API remains generic and non-enumerating.
        }
        return "If the account exists and is not yet verified, a new verification email has been queued.";
    }

    public String forgotPassword(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        User user = userRepository.findById(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordResetToken(UUID.randomUUID().toString());
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(normalizedEmail, user.getPasswordResetToken());
        return "Password reset token generated successfully.";
    }

    public String resetPassword(String email, String token, String newPassword) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findById(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPasswordResetToken() == null || !user.getPasswordResetToken().equals(token)) {
            throw new IllegalArgumentException("Invalid or expired password reset token.");
        }

        if (user.getPasswordResetTokenExpiresAt() != null && user.getPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired.");
        }

        validatePassword(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        userRepository.save(user);
        return "Password reset successfully.";
    }

    public void updateUser(String userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO == null) {
            throw new IllegalArgumentException("Profile update payload is required.");
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().equalsIgnoreCase(userId)) {
            throw new IllegalArgumentException("Email changes require a verification workflow.");
        }

        if (userDTO.getFirstName() != null) {
            String firstName = userDTO.getFirstName().trim();
            if (firstName.isBlank()) {
                throw new IllegalArgumentException("First name cannot be blank.");
            }
            user.setFirstName(firstName);
        }

        if (userDTO.getLastName() != null) {
            String lastName = userDTO.getLastName().trim();
            if (lastName.isBlank()) {
                throw new IllegalArgumentException("Last name cannot be blank.");
            }
            user.setLastName(lastName);
        }

        if (userDTO.getDob() != null) {
            String dob = userDTO.getDob().trim();
            if (dob.isBlank()) {
                throw new IllegalArgumentException("Date of birth cannot be blank.");
            }
            try {
                LocalDate.parse(dob);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Date of birth must be in YYYY-MM-DD format.");
            }
            user.setDob(dob);
        }

        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    public UserDTO getAuthenticatedUser() {
        String userId = authenticationHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user);
    }

    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user);
    }

    public User getUserDetailById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }
}
