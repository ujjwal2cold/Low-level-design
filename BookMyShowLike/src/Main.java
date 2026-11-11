import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
   public static void main(String[] args) {
      List<Seat> seats = new ArrayList<>();
      seats.add(new Seat("A1", SEAT_TYPE.ORDINARY, 200));
      seats.add(new Seat("A2", SEAT_TYPE.ORDINARY, 200));
      seats.add(new Seat("A3", SEAT_TYPE.RECLINER, 400));
      Screen s = new Screen(seats);
      Theatre theatre = new Theatre("XYZ", "WASD", List.of(s));
      IShowRepository repo = new ShowDB();
      BookMyShow bms = new BookMyShow(List.of(theatre), repo);
      Movie m0 = new Movie("Interstellar", "Scifi", new ArrayList<>(), " ", 120);
      Movie m1 = new Movie("MI", "Action", new ArrayList<>(), " ", 120);
      Show s0 = new Show(m0, theatre, s), s1 = new Show(m1, theatre, s);
      System.out.println(bms.tryAddingShow(s0));
      System.out.println(bms.tryAddingShow(s1));
      BookMyShowService service = new BookMyShowService(bms, new MasterCard(), repo);
      User u = new User(1, "John");
      Optional<Ticket> t0 = service.tryLockingSeat(u, s0.getId(), seats.getFirst()), t1 = service.tryLockingSeat(u, s0.getId(), seats.getFirst());
      System.out.println(t0);
      System.out.println(t1);
      boolean cancelled = service.cancelBooking(t0.get());
      System.out.println("Ticket cancelled : " + cancelled);
      System.out.println(service.tryLockingSeat(u, s0.getId(), seats.getFirst()));
   }
}
