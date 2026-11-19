package models;

import enums.ChannelType;
import enums.DeliveryStatus;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DeliveryTask {
   private Notification notification;
   private final Map<ChannelType, DeliveryStatus> map = new ConcurrentHashMap<>();

   public DeliveryTask(Notification notification) {
      this.notification = notification;
      Set<ChannelType> pref = notification.getUser().preferred(notification.getType());
      for (ChannelType each : pref)
         map.put(each, DeliveryStatus.IN_PROGRESS);
   }

   public void setFinalStatus(ChannelType type, DeliveryStatus status) {
      map.put(type, status);
   }

}
