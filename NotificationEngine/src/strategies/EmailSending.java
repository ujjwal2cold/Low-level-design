package strategies;

import models.Notification;

import java.util.Random;

public class EmailSending implements ISendingStrategy {
   @Override
   public boolean sendNotification(Notification n) {
      String email = n.getUser().getEmail();
      try {
         Thread.sleep(new Random().nextInt(1000, 2000));
      } catch (InterruptedException e) {
         System.out.println(e.getMessage());
      }
      if (new Random().nextBoolean()) {
         System.out.println("SUCCESSFULLY sent email to : " + email);
         return true;
      }
      System.out.println("FAILED to send email to : " + email);
      return false;
   }
}
