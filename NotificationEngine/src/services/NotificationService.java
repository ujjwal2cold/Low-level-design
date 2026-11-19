package services;

import enums.ChannelType;
import factories.NotificationChannelFactory;
import models.Notification;
import strategies.ISendingStrategy;

public class NotificationService {
   public boolean sendNotification(Notification n, ChannelType type) {
      ISendingStrategy strat = NotificationChannelFactory.makeChannel(type);
      return strat.sendNotification(n);
   }
}
