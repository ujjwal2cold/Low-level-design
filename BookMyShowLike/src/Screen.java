import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Screen {
   private static AtomicInteger autoId = new AtomicInteger(0);
   private final AtomicReference<Show> showReference = new AtomicReference<>(null);
   private int id;
   private List<Seat> allSeats;

   Screen(List<Seat> allSeats) {
      this.allSeats = List.copyOf(allSeats);
   }

   public int getId() {
      return id;
   }

   public boolean setShow(Show show) {
      boolean res = showReference.compareAndSet(null, show);
      System.out.println("res= " + res);
      return res;
   }

   public List<Seat> getAllSeats() {
      return allSeats;
   }
}
