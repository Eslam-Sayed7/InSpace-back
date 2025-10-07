package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Fetch all notifications for a specific user
    List<Notification> findByUserId(Long userId);

    // Fetch unread notifications for a specific user
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
}
