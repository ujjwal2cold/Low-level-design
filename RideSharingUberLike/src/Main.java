import Models.Driver;
import Models.Location;
import Models.Ride;
import Models.Rider;
import Repositories.DriverRepository;
import Services.FareCalculationService;
import Services.RideMatchingService;
import Services.RideService;
import Strategies.DriverMatching.NearestDriverMatching;
import Strategies.FareCalculation.DistanceBaseFare;
import enums.PRODUCT_TYPE;

public class Main {
   public static void main(String[] args) {
      DriverRepository repo = new DriverRepository();
      Driver d0 = new Driver("WASD"), d1 = new Driver("XYZ");
      repo.addDriver(d0);
      repo.addDriver(d1);
      d0.addProduct(PRODUCT_TYPE.UBER_XL);
      d1.addProduct(PRODUCT_TYPE.UBER_XL);
      d0.setCurrentLocation(new Location(10, 10));
      d1.setCurrentLocation(new Location(20, 20));
      Rider r1 = new Rider("R1"), r2 = new Rider("R2");
      Location src = new Location(0, 0), dest = new Location(50, 50);
      r1.setCurrentLocation(src);
      r2.setCurrentLocation(src);
      RideMatchingService match = new RideMatchingService(new NearestDriverMatching(repo, 50));
      FareCalculationService fare = new FareCalculationService(new DistanceBaseFare());
      RideService service = new RideService(match, fare, repo);
      Runnable task1 = () -> {
         service.getFareEstimates(src, dest);
         Ride r = service.makeRide(r1.getId(), src, dest, PRODUCT_TYPE.UBER_XL);
         service.tryBooking(r);
      };
      Runnable task2 = () -> {
         service.getFareEstimates(src, dest);
         Ride r = service.makeRide(r2.getId(), src, dest, PRODUCT_TYPE.UBER_XL);
//         try {
//            Thread.sleep(20000);
//         } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//         }
         service.tryBooking(r);
      };
      Thread t1 = new Thread(task1);
      Thread t2 = new Thread(task2);
      t1.start();
      t2.start();
   }
}
