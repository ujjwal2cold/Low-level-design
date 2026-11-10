package parkinglot;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class ParkingLotService {
   ParkingLot parkingLot;
   TicketRepository repo;
   volatile FareCalculator fc;
   ReentrantLock lock;

   public ParkingLotService(ParkingLot parkingLot, TicketRepository repo) {
      this.parkingLot = parkingLot;
      this.repo = repo;
      this.lock = new ReentrantLock();
   }

   public void setFareCalculator(FareCalculator fc) {
      this.fc = fc;
   }

   public Optional<Ticket> park(Vehicle v) {
      lock.lock();
      try {
         Optional<ParkingSpot> spot = parkingLot.getParkingSpot(v.getType());
         return spot.map(parkingSpot -> generateTicket(parkingSpot, v));
      } finally {
         lock.unlock();
      }
   }

   private Ticket generateTicket(ParkingSpot parkingSpot, Vehicle v) {
      Ticket t = new Ticket(parkingSpot, v, new Date());
      repo.addTicket(t);
      parkingSpot.book();
      return t;
   }

   public long checkout(int id) {
      lock.lock();
      try {
         Ticket ticket = repo.getTicketById(id);
         if (ticket.getStatus() == STATUS.VALID) {
            ticket.getSpot().unbook();
            ticket.setStatus(STATUS.INVALID);
            return fc.calculateFare(ticket.getNow());
         }
         throw new IllegalArgumentException("Ticket Invalid");
      } finally {
         lock.unlock();
      }
   }
}
