package org.colcum.admin.domain.notification.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.colcum.admin.domain.notification.api.dto.NotificationSendRequest;
import org.colcum.admin.domain.notification.application.NotificationService;
import org.colcum.admin.global.common.api.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationSseController {

    private final NotificationService notificationService;

    @GetMapping(value = "/connect/receiverId", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ApiResponse<SseEmitter> connect(@PathVariable Long receiverId) {
        notificationService.add(receiverId);
        return new ApiResponse<>(HttpStatus.OK.value(), "connected", null);
    }

    @PostMapping("/send")
    public void sendNotification(
        @RequestBody NotificationSendRequest sendRequest
    ) {
        notificationService.sendNotification(sendRequest);
    }

}
