package Strategies.DriverMatching;

import Models.Driver;
import Models.Location;
import Models.Ride;
import Repositories.DriverRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NearestDriverMatching implements IDriverMatchingStrat {
   private final DriverRepository repo;
   private int maxAllowedRadius;

   public NearestDriverMatching(DriverRepository repo, int maxAllowedRadius) {
      this.repo = repo;
      this.maxAllowedRadius = maxAllowedRadius;
   }

   @Override
   public Optional<String> tryMatchingAndBook(Ride r) {
      Location src = r.getSrcLocation();
      List<Driver> candidateDrivers = new ArrayList<>();
      int bestDist = Integer.MAX_VALUE;
      for (Driver d : repo.allDrivers()) {
         if (!d.isAvailable() || !d.supportsProduct(r.getType())) continue;
         int dist = src.getDistanceFrom(d.getCurrentLocation());
         if (dist <= maxAllowedRadius && dist < bestDist) {
            candidateDrivers.add(d);
         }
      }
      for (Driver d : candidateDrivers) {
         switch (r.checkAndAssignDriver(d)) {
            case RIDE_BOOKED -> {
               return Optional.of(d.getId());
            }
            case RIDE_EXPIRED -> {
               return Optional.empty();
            }
            case RIDE_UNBOOKED -> {
               /* try for next in line candidate */
            }
         }
      }
      return Optional.empty();
   }
}
