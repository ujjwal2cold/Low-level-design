package models;

import enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Notification {
   private final String notificationId;
   private final User user;
   private final NotificationType type;
   private String message;
}
