import java.util.Optional;

public class BookMyShowService {
   private BookMyShow bms;
   private volatile IPaymentStrategy ips;
   private IShowRepository repo;

   public BookMyShowService(BookMyShow bms, IPaymentStrategy ips, IShowRepository repo) {
      this.bms = bms;
      this.ips = ips;
      this.repo = repo;
   }

   public void setPaymentStrategy(IPaymentStrategy ips) {
      this.ips = ips;
   }

   public Optional<Ticket> tryLockingSeat(User u, int showId, Seat seat) {
      Show s = repo.findShowById(showId);
      if (s == null)
         throw new IllegalArgumentException("Invalid show id: " + showId);
      boolean res = bms.tryBookingShow(u, repo.findShowById(showId), seat);
      if (res) {
         ips.pay(seat.getPrice());
         boolean confirm = s.confirmSeat(u, seat);
         if (!confirm) {
            ips.refund(seat.getPrice());
            return Optional.empty();
         }
         return Optional.of(new Ticket(showId, seat.getId(), u));
      }
      return Optional.empty();
   }

   public boolean cancelBooking(Ticket ticket) {
      Show s = repo.findShowById(ticket.getShowId());
      if (s == null)
         throw new IllegalArgumentException("Ticket is invalid");
      return s.cancelSeat(ticket.getSeatId(), ticket.getUser());
   }
}
