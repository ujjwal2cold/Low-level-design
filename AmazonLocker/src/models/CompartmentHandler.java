package models;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public abstract class CompartmentHandler {
   @Setter
   @Getter
   private CompartmentHandler next;

   abstract boolean canFit(Compartment newCompartment);

   abstract boolean canFit(Order o);

   abstract Optional<Compartment> tryBookingInAgnosticHandler(Order o);


   void addCompartment(Compartment newCompartment) {
      if (canFit(newCompartment)) {
         this.addCompartment(newCompartment);
      } else if (this.next != null) {
         this.getNext().addCompartment(newCompartment);
      }
   }


   public Optional<Compartment> tryBooking(Order o) {
      if (canFit(o)) {
         Optional<Compartment> answer = tryBookingInAgnosticHandler(o);
         if (answer.isPresent())
            return answer;
      }
      if (this.next != null)
         return this.next.tryBooking(o);
      return Optional.empty();
   }
}
