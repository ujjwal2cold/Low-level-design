package strategies;

import models.Compartment;
import models.Locker;
import models.Order;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NearestFirst implements ILockerFindingStrategy {
   @Override
   public Optional<Compartment> tryBooking(List<Locker> allLocker, Order o) {
      allLocker.sort(Comparator.comparingInt(e -> o.getLoc().getDistance(e.getLoc())));
      for (Locker x : allLocker) {
         Optional<Compartment> res = x.tryBooking(o);
         if (res.isPresent())
            return res;
      }
      return Optional.empty();
   }
}
