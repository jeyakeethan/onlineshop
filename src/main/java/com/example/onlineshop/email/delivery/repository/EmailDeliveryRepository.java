package com.example.onlineshop.email.delivery.repository;

import com.example.onlineshop.email.delivery.entity.EmailDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface EmailDeliveryRepository extends JpaRepository<EmailDelivery, Long> {
    Optional<EmailDelivery> findByDeduplicationKey(String deduplicationKey);
    List<EmailDelivery> findTop100ByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            List<String> statuses, LocalDateTime nextAttemptAt);
}
