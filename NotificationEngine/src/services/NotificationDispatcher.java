package services;

import enums.ChannelType;
import enums.DeliveryStatus;
import models.DeliveryTask;
import models.Notification;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationDispatcher {
   private NotificationService svc = new NotificationService();
   private ExecutorService exe = Executors.newFixedThreadPool(5);

   public void dispatch(DeliveryTask task) {
      Notification notification = task.getNotification();
      Set<ChannelType> comms = task.getNotification().getUser().preferred(notification.getType());
      for (ChannelType each : comms) {
         attempt(notification, each, 3).thenAccept((x) -> task.setFinalStatus(each, x ? DeliveryStatus.SUCCESS : DeliveryStatus.FAILED));
      }
   }

   private CompletableFuture<Boolean> attempt(Notification notification, ChannelType each, int retriesLeft) {
      CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() -> svc.sendNotification(notification, each), exe);
      return result.handle((success, ex) -> ex == null && success).thenCompose(v -> {
         if (v || retriesLeft == 0) {
            return CompletableFuture.completedFuture(v);
         }
         return attempt(notification, each, retriesLeft - 1);
      });
   }

   public void shutDown() {
      exe.shutdown();
   }
}
