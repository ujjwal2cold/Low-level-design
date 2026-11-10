package parkinglot;

import java.util.List;
import java.util.Optional;

public class Main {
   public static void main(String[] args) {
      ParkingLot lot = new ParkingLot(List.of(
            new ParkingSpot(1, VehicleType.CAR),
            new ParkingSpot(2, VehicleType.BIKE)));

      ParkingLotService service = new ParkingLotService(lot, new InMemoryTicketRepository());
      service.setFareCalculator(new HourlyFareCalculator(20));

      Optional<Ticket> ticket = service.park(new Vehicle("PB-10-1234", VehicleType.CAR));
      ticket.ifPresent(t -> System.out.println("Fare: " + service.checkout(t.getId())));
   }
}
