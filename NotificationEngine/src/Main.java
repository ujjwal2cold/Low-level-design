import enums.ChannelType;
import enums.NotificationType;
import models.DeliveryTask;
import models.Notification;
import models.User;
import services.NotificationDispatcher;

public class Main {
   public static void main(String[] args) throws Exception {
      User u = new User("WASD", "Ujjwal", "IMissHer@4everInMyHeart", "Ab tak yaad hai");
      u.addPreference(NotificationType.SECURITY, ChannelType.EMAIL);
      u.addPreference(NotificationType.SECURITY, ChannelType.SMS);
      NotificationDispatcher dispatcher = new NotificationDispatcher();
      Notification n = new Notification("XYZ", u, NotificationType.SECURITY, "Your account has been compromised");
      dispatcher.dispatch(new DeliveryTask(n));
      Thread.sleep(20000);
      dispatcher.shutDown();
   }
}
