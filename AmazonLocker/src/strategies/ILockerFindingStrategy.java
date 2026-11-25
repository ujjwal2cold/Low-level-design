package strategies;

import models.Compartment;
import models.Locker;
import models.Order;

import java.util.List;
import java.util.Optional;

public interface ILockerFindingStrategy {
   Optional<Compartment> tryBooking(List<Locker> allLocker, Order o);
}
