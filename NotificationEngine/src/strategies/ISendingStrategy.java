package strategies;

import models.Notification;

public interface ISendingStrategy {
   boolean sendNotification(Notification n);
}
