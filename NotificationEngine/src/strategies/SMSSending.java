package strategies;

import models.Notification;

import java.util.Random;

public class SMSSending implements ISendingStrategy {
   @Override
   public boolean sendNotification(Notification n) {
      String number = n.getUser().getMobileNumber();
      try {
         Thread.sleep(new Random().nextInt(1000, 2000));
      } catch (InterruptedException e) {
         System.out.println(e.getMessage());
      }
      if (new Random().nextBoolean()) {
         System.out.println("SUCCESSFULLY sent message to : " + number);
         return true;
      }
      System.out.println("FAILED to send message to : " + number);
      return false;
   }
}
