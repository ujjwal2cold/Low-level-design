package model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Subscriber implements ISubscriber {
   private int id;

   @Override
   public int getId() {
      return id;
   }

   @Override
   public void doWork(String message) {
      System.out.println("Subscriber: " + getId() + " has consumed " + message);
   }
}
