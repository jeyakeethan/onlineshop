package com.example.onlineshop.notification.repository;

import com.example.onlineshop.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByDeduplicationKey(String deduplicationKey);
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    @Modifying
    @Query("update Notification notification set notification.read = true "
            + "where notification.userId = :userId and notification.read = false")
    int markAllAsReadByUserId(@Param("userId") String userId);
}
