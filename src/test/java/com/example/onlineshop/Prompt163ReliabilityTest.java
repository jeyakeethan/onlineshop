package com.example.onlineshop;

import com.example.onlineshop.audit.controller.AdminAuditController;
import com.example.onlineshop.admin.controller.AdminDashboardController;
import com.example.onlineshop.config.FallbackCacheConfig;
import com.example.onlineshop.order.dto.OrderEvent;
import com.example.onlineshop.order.entity.OutboxEvent;
import com.example.onlineshop.order.service.OrderProducerService;
import com.example.onlineshop.order.service.TransactionalOutboxService;
import com.example.onlineshop.order.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Prompt163ReliabilityTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private OrderProducerService orderProducerService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionalOutboxService transactionalOutboxService;

    @Test
    void outboxDispatch_recoversAfterUnavailableProducerWithoutLosingEvent() throws Exception {
        OrderEvent event = new OrderEvent(163L, OrderEvent.Status.PENDING, null);
        OutboxEvent outbox = new OutboxEvent();
        outbox.setPayload("{}");
        outbox.setStatus("PENDING");
        outbox.setAttempts(0);
        outbox.setNextAttemptAt(LocalDateTime.now().minusSeconds(1));

        when(objectMapper.readValue("{}", OrderEvent.class)).thenReturn(event);
        when(orderProducerService.publish(event)).thenReturn(false, true);

        transactionalOutboxService.dispatchOne(outbox);

        assertEquals("PENDING", outbox.getStatus());
        assertEquals(1, outbox.getAttempts());
        assertNotNull(outbox.getLastError());
        assertTrue(outbox.getNextAttemptAt().isAfter(LocalDateTime.now()));

        outbox.setNextAttemptAt(LocalDateTime.now().minusSeconds(1));
        transactionalOutboxService.dispatchOne(outbox);

        assertEquals("PUBLISHED", outbox.getStatus());
        assertNotNull(outbox.getPublishedAt());
        assertNull(outbox.getLastError());
        verify(outboxEventRepository, times(2)).save(outbox);
    }

    @Test
    void fallbackCache_isBoundedAndRejectsInvalidConfiguration() {
        CacheManager manager = new FallbackCacheConfig().fallbackCacheManager(2, 10);
        manager.getCache("reliability").put("key", "value");

        assertEquals("value", manager.getCache("reliability").get("key", String.class));
        assertThrows(IllegalArgumentException.class,
                () -> new FallbackCacheConfig().fallbackCacheManager(0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new FallbackCacheConfig().fallbackCacheManager(2, 0));
    }

    @Test
    void protectedAdminControllers_requireExplicitAuthorization() throws Exception {
        PreAuthorize dashboardAuthorization = AdminDashboardController.class
                .getDeclaredMethod("getDashboard", java.time.LocalDate.class, java.time.LocalDate.class)
                .getAnnotation(PreAuthorize.class);
        PreAuthorize auditAuthorization = AdminAuditController.class
                .getDeclaredMethod("getAudit", int.class, int.class)
                .getAnnotation(PreAuthorize.class);

        assertNotNull(dashboardAuthorization);
        assertTrue(dashboardAuthorization.value().contains("hasAnyRole"));
        assertNotNull(auditAuthorization);
        assertTrue(auditAuthorization.value().contains("hasAnyRole"));
    }
}
