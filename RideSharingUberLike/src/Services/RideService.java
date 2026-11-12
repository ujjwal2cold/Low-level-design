package Services;

import Models.Location;
import Models.Ride;
import Repositories.DriverRepository;
import enums.PRODUCT_TYPE;

import java.util.Map;
import java.util.Optional;

public class RideService {
   private final RideMatchingService rideService;
   private final FareCalculationService fareService;
   private final DriverRepository repo;

   public RideService(RideMatchingService rideService, FareCalculationService fareService, DriverRepository repo) {
      this.rideService = rideService;
      this.fareService = fareService;
      this.repo = repo;
   }

   public void getFareEstimates(Location src, Location dest) {
      Map<PRODUCT_TYPE, Integer> map = fareService.makeAllFares(src, dest);
      for (PRODUCT_TYPE type : map.keySet()) {
         System.out.println(type + ": " + map.get(type));
      }
   }

   public Ride makeRide(String riderId, Location src, Location dest, PRODUCT_TYPE type) {
      return new Ride(riderId, src, dest, type, fareService.getFare(src, dest, type));
   }

   public void tryBooking(Ride r) {
      Optional<String> res = rideService.getDriverForRide(r);
      if (res.isPresent()) {
         System.out.println("Driver booked successfully with id : " + res.get() + " for Thread: " + Thread.currentThread().getName());
      } else
         System.out.println("Please try booking again");
   }
}
