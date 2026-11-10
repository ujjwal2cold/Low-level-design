public class Ticket {
   private int showId;
   private String seatId;
   private User u;

   Ticket(int showId, String seatId, User u) {
      this.showId = showId;
      this.seatId = seatId;
      this.u = u;
   }

   public int getShowId() {
      return showId;
   }
   public User getUser() {
      return u;
   }

   public String getSeatId() {
      return seatId;
   }

   @Override
   public String toString() {
      return "Ticket{" +
              "showId=" + showId +
              ", seatId='" + seatId + '\'' +
              '}';
   }
}
