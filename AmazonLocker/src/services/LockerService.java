package services;

import lombok.Getter;
import models.Compartment;
import models.Location;
import models.Locker;
import models.Order;
import strategies.ILockerFindingStrategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class LockerService {
   @Getter
   private final List<Locker> lockers;

   public LockerService(ILockerFindingStrategy strat) {
      this.lockers = new CopyOnWriteArrayList<>();
   }

   public void addLocker(String lockerId, Location loc) {
      Locker lck = new Locker(lockerId, loc);
      lockers.add(lck);
   }

   private Optional<Locker> findById(String id) {
      for (Locker x : lockers) {
         if (x.getLockerId().equals(id))
            return Optional.of(x);
      }
      return Optional.empty();
   }

   public Optional<Compartment> tryAssigningOrder(String lockerId, Order o) {
      Optional<Locker> lck = findById(lockerId);
      if (lck.isPresent()) {
         return lck.get().tryBooking(o);
      }
      return Optional.empty();
   }

   public void addCompartment(String lockerId, Compartment newCompartment) {
      Optional<Locker> lck = findById(lockerId);
      lck.ifPresent(locker -> locker.addCompartment(newCompartment));
   }

   public void releaseCompartment(Compartment c, Order o) {
      c.tryReleasing(o);
   }
}
