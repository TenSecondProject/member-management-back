package org.colcum.admin.domain.notification.api.dto;

import lombok.Data;
import org.colcum.admin.domain.notification.domain.type.NotificationType;

import java.util.List;

@Data
public class NotificationSendRequest {

    private Long senderId;

    private List<Long> receiversList;

    private NotificationType notificationType;

    private String message;

}
