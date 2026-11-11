import java.util.concurrent.locks.ReentrantLock;

public class ShowSeat {
   private final Seat seat;
   private SEAT_STATUS status = SEAT_STATUS.AVAILABLE;
   private User lockedByUser;
   private long lockExpiry;
   private final long TTL = 5 * 1000 * 60;
   private final ReentrantLock lock;

   public ShowSeat(Seat seat) {
      this.seat = seat;
      this.lock = new ReentrantLock();
   }

   public boolean ifAvailableThenLock(User u, long time) {
      lock.lock();
      try {
         boolean res = (this.status == SEAT_STATUS.AVAILABLE || this.status == SEAT_STATUS.LOCKED && this.lockExpiry <= time);
         if (res)
            lockSeat(u, time);
         return res;
      } finally {
         lock.unlock();
      }
   }

   private void lockSeat(User u, long time) {
      setLockedByUser(u);
      setStatus(SEAT_STATUS.LOCKED);
      setLockExpiry(time + TTL);
   }

   public boolean ifValidThenBook(User u) {
      lock.lock();
      try {
         boolean res = this.lockedByUser == u && this.lockExpiry > System.currentTimeMillis();
         if (res) {
            setLockExpiry(0);
            setStatus(SEAT_STATUS.BOOKED);
         }
         return res;
      } finally {
         lock.unlock();
      }
   }

   public boolean ifValidThenRelease(User u) {
      lock.lock();
      try {
         boolean valid = this.lockedByUser == u && this.status == SEAT_STATUS.BOOKED;
         if (valid) {
            setStatus(SEAT_STATUS.AVAILABLE);
            setLockExpiry(0);
            setLockedByUser(null);
         }
         return valid;
      } finally {
         lock.unlock();
      }
   }

   private void setLockExpiry(long lockExpiry) {
      this.lockExpiry = lockExpiry;
   }

   private void setLockedByUser(User lockedByUser) {
      this.lockedByUser = lockedByUser;
   }

   private void setStatus(SEAT_STATUS status) {
      this.status = status;
   }


}
