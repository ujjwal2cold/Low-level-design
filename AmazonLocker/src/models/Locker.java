package models;

import lombok.Getter;

import java.util.Optional;

public class Locker {
   @Getter
   private final String lockerId;
   @Getter
   private final Location loc;
   private final CompartmentHandler chain;
   public Locker(String lockerId, Location loc) {
      this.lockerId = lockerId;
      this.loc = loc;
      SmallCompartmentHandler small = new SmallCompartmentHandler();
      MediumCompartmentHandler med = new MediumCompartmentHandler();
      small.setNext(med);
      med.setNext(null);
      this.chain = small;
   }

   public void addCompartment(Compartment newCompartment) {
      this.chain.addCompartment(newCompartment);
   }

   public Optional<Compartment> tryBooking(Order o) {
      return this.chain.tryBooking(o);
   }
}
