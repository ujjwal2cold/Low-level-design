import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Show {
   private static AtomicInteger autoId = new AtomicInteger(0);
   private int id;
   private Movie mov;
   private Theatre theatre;
   private Screen screen;
   private final Map<String, ShowSeat> map = new HashMap<>();
   private ReentrantLock lock;

   Show(Movie mov, Theatre theatre, Screen screen) {
      this.id = autoId.getAndIncrement();
      this.mov = mov;
      this.theatre = theatre;
      this.lock = new ReentrantLock();
      this.screen = screen;
      for (Seat each : screen.getAllSeats()) {
         map.put(each.getId(), new ShowSeat(each));
      }
   }

   public int getId() {
      return id;
   }

   public int getTheatreId() {
      return theatre.getId();
   }

   public int getScreenId() {
      return screen.getId();
   }

   public boolean tryLockingSeat(User u, Seat seat) {
      lock.lock();
      try {
         ShowSeat currStatus = map.get(seat.getId());
         if (currStatus == null)
            throw new IllegalArgumentException("Seat with id " + seat.getId() + " does not exist");
         long tmp = System.currentTimeMillis();
         if (currStatus.getStatus() == SEAT_STATUS.AVAILABLE || (currStatus.getStatus() == SEAT_STATUS.LOCKED && currStatus.getLockExpiry() < tmp)) {
            currStatus.setStatus(SEAT_STATUS.LOCKED);
            currStatus.setLockExpiry(tmp + 5 * 1000 * 60);
            currStatus.setLockedByUser(u);
            return true;
         }
         return false;
      } finally {
         lock.unlock();
      }
   }

   public boolean confirmSeat(User u, Seat seat) {
      lock.lock();
      try {
         ShowSeat currStatus = map.get(seat.getId());
         if (currStatus.getStatus() == SEAT_STATUS.LOCKED && currStatus.getLockedByUser() == u && currStatus.getLockExpiry() > System.currentTimeMillis()) {
            currStatus.setStatus(SEAT_STATUS.BOOKED);
            return true;
         }
         return false;
      } finally {
         lock.unlock();
      }
   }

   public void cancelSeat(String seatId, User u) {
      lock.lock();
      try {
         ShowSeat currStatus = map.get(seatId);
         if (currStatus == null)
            throw new IllegalArgumentException("Seat with id " + seatId + " does not exist");
         if (currStatus.getStatus() == SEAT_STATUS.BOOKED && currStatus.getLockedByUser() == u) {
            currStatus.setStatus(SEAT_STATUS.AVAILABLE);
            currStatus.setLockedByUser(null);
            return;
         }
         throw new IllegalArgumentException("Invalid Credentials");
      } finally {
         lock.unlock();
      }
   }
}
