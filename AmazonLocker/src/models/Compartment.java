package models;

import enums.CompartmentStatus;
import enums.CompartmentType;

public abstract class Compartment {
   private final String lockerId;

   public Compartment(String lockerId) {
      this.lockerId = lockerId;
   }

   abstract String getId();

   abstract CompartmentType getType();

   abstract CompartmentStatus getStatus();

   abstract boolean tryBooking(Order o);

   public abstract boolean tryReleasing(Order o);
}
