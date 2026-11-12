package Services;

import Models.Ride;
import Strategies.DriverMatching.IDriverMatchingStrat;

import java.util.Optional;

public class RideMatchingService {
   volatile IDriverMatchingStrat strat;

   public RideMatchingService(IDriverMatchingStrat strat) {
      this.strat = strat;
   }

   public void setStrat(IDriverMatchingStrat strat) {
      this.strat = strat;
   }

   Optional<String> getDriverForRide(Ride r) {
      return strat.tryMatchingAndBook(r);
   }
}
