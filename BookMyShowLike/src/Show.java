import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Show {
   private static AtomicInteger autoId = new AtomicInteger(0);
   private int id;
   private Movie mov;
   private Theatre theatre;
   private Screen screen;
   private final Map<String, ShowSeat> map = new HashMap<>();

   Show(Movie mov, Theatre theatre, Screen screen) {
      this.id = autoId.getAndIncrement();
      this.mov = mov;
      this.theatre = theatre;
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
      ShowSeat showAgnosticSeat = map.get(seat.getId());
      if (showAgnosticSeat == null)
         throw new IllegalArgumentException("Seat with id " + seat.getId() + " does not exist");
      long time = System.currentTimeMillis();
      return showAgnosticSeat.ifAvailableThenLock(u, time);
   }

   public boolean confirmSeat(User u, Seat seat) {
      ShowSeat showAgnosticSeat = map.get(seat.getId());
      return showAgnosticSeat.ifValidThenBook(u);
   }

   public boolean cancelSeat(String seatId, User u) {
      ShowSeat showAgnosticSeat = map.get(seatId);
      if (showAgnosticSeat == null)
         throw new IllegalArgumentException("Seat with id " + seatId + " does not exist");
      return showAgnosticSeat.ifValidThenRelease(u);
   }
}
