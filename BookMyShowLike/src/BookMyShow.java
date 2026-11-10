import java.util.List;

public class BookMyShow {
   private final List<Theatre> theatreList;
   private final IShowRepository repo;

   public BookMyShow(List<Theatre> theatreList, IShowRepository repo) {
      this.theatreList = List.copyOf(theatreList);
      this.repo = repo;
   }

   public boolean tryAddingShow(Show show) {
      for (Theatre theatre : theatreList) {
         if (theatre.getId() == show.getTheatreId()) {
            if (theatre.tryAddingShow(show)) {
               repo.addShow(show);
               return true;
            }
         }
      }
      return false;
   }

   public boolean tryBookingShow(User u, Show show, Seat seat) {
      return show.tryLockingSeat(u, seat);
   }
}
