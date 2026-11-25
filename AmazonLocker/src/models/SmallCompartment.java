package models;

import enums.CompartmentStatus;
import enums.CompartmentType;

import java.util.concurrent.atomic.AtomicReference;

public class SmallCompartment extends Compartment {
   private final String id;
   private final CompartmentType type = CompartmentType.SMALL;
   private final AtomicReference<Order> ref = new AtomicReference<>(null);

   public SmallCompartment(String lockerId, String compId) {
      super(lockerId);
      this.id = compId;
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public CompartmentType getType() {
      return type;
   }

   @Override
   public CompartmentStatus getStatus() {
      return ref.get() == null ? CompartmentStatus.FREE : CompartmentStatus.BOOKED;
   }

   @Override
   public boolean tryBooking(Order o) {
      return ref.compareAndSet(null, o);
   }

   public boolean tryReleasing(Order o) {
      return ref.compareAndSet(o, null);
   }

   @Override
   public String toString() {
      return "SmallCompartment{" +
              "id='" + id + '\'' +
              ", type=" + type +
              '}';
   }
}
