public class ShowSeat {
   private Seat seat;
   private SEAT_STATUS status = SEAT_STATUS.AVAILABLE;
   private User lockedByUser;
   private long lockExpiry;

   public ShowSeat(Seat seat) {
      this.seat = seat;
   }

   public void setLockExpiry(long lockExpiry) {
      this.lockExpiry = lockExpiry;
   }

   public SEAT_STATUS getStatus() {
      return status;
   }

   public void setLockedByUser(User lockedByUser) {
      this.lockedByUser = lockedByUser;
   }

   public User getLockedByUser() {
      return this.lockedByUser;
   }

   public void setStatus(SEAT_STATUS status) {
      this.status = status;
   }

   public long getLockExpiry() {
      return lockExpiry;
   }
}
