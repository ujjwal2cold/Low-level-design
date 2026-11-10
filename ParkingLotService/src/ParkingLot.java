package parkinglot;

import java.util.List;
import java.util.Optional;

public class ParkingLot {
   private final List<ParkingSpot> spots;

   public ParkingLot(List<ParkingSpot> spots) {
      this.spots = spots;
   }

   public Optional<ParkingSpot> getParkingSpot(VehicleType type) {
      return spots.stream()
            .filter(s -> s.getType() == type && s.isAvailable())
            .findFirst();
   }
}
