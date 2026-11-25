package models;

import enums.CompartmentType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class SmallCompartmentHandler extends CompartmentHandler {
   private final List<SmallCompartment> smallCompartments = new CopyOnWriteArrayList<>();

   @Override
   boolean canFit(Compartment newCompartment) {
      return newCompartment.getType() == CompartmentType.SMALL;
   }

   boolean canFit(Order o) {
      return CompartmentType.SMALL.canFit(o.getVolume(), o.getWeight());
   }

   @Override
   void addCompartment(Compartment newCompartment) {
      smallCompartments.add((SmallCompartment) newCompartment);
   }

   @Override
   Optional<Compartment> tryBookingInAgnosticHandler(Order o) {
      for (SmallCompartment x : smallCompartments) {
         if (x.tryBooking(o))
            return Optional.of(x);
      }
      return Optional.empty();
   }


}
