package org.colcum.admin.domain.notification.application;

import lombok.extern.slf4j.Slf4j;
import org.colcum.admin.domain.notification.api.dto.NotificationSendRequest;
import org.springframework.jmx.export.notification.UnableToSendNotificationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long receiverId) {
        SseEmitter emitter = new SseEmitter();
        this.emitters.put(receiverId, emitter);
        log.info("new emitter add: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(receiverId);
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });
        emitter.onError((e) -> {
            log.info("onError callback");
            this.emitters.remove(receiverId);
        });

        sendInitialConnectMessage(emitter);
        return emitter;
    }

    public void sendNotification(NotificationSendRequest sendRequest) {
        for (int i = 0; i < sendRequest.getReceiversList().size(); i++) {
            Long receiverId = sendRequest.getReceiversList().get(i);
            SseEmitter sseEmitter = emitters.get(receiverId);

            if (sseEmitter == null) continue;

            try {
                sseEmitter.send(SseEmitter.event().name("notification").data(sendRequest.getMessage()));
            } catch (Exception e) {
                log.info("send fail, senderId: {}, receiverId: {}", sendRequest.getSenderId(), receiverId);
            }

        }
    }

    private void sendInitialConnectMessage(SseEmitter emitter) {
        try {
            emitter.send(
                SseEmitter.event()
                    .name("connect")
                    .data("connected")
            );
        } catch (IOException e) {
            throw new UnableToSendNotificationException(e.getMessage());
        }
    }

}
