package models;


import enums.CompartmentType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class MediumCompartmentHandler extends CompartmentHandler {
   private final List<MediumCompartment> medCompartments = new CopyOnWriteArrayList<>();

   @Override
   boolean canFit(Compartment newCompartment) {
      return newCompartment.getType() == CompartmentType.MEDIUM;
   }

   boolean canFit(Order o) {
      return CompartmentType.MEDIUM.canFit(o.getVolume(), o.getWeight());
   }

   @Override
   void addCompartment(Compartment newCompartment) {
      medCompartments.add((MediumCompartment) newCompartment);
   }

   @Override
   Optional<Compartment> tryBookingInAgnosticHandler(Order o) {
      for (MediumCompartment x : medCompartments) {
         if (x.tryBooking(o))
            return Optional.of(x);
      }
      return Optional.empty();
   }

}

