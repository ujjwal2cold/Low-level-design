package services;

import models.Compartment;
import models.Location;
import models.Locker;
import models.Order;
import strategies.ILockerFindingStrategy;

import java.util.Optional;

public class AmazonLocker {
   private final LockerService lckService;
   private final ILockerFindingStrategy strat;

   public AmazonLocker(LockerService svc, ILockerFindingStrategy strat) {
      this.lckService = svc;
      this.strat = strat;
   }

   public void addLocker(String lockerId, Location loc) {
      lckService.addLocker(lockerId, loc);
   }

   public void addCompartment(String lockerId, Compartment c) {
      lckService.addCompartment(lockerId, c);
   }

   public Optional<Compartment> tryBooking(Order o) {
      return strat.tryBooking(lckService.getLockers(), o);
   }

   public void release(Compartment c, Order o) {
      lckService.releaseCompartment(c, o);
   }
}
