package Models;

import enums.USER_TIER;

import java.util.concurrent.atomic.AtomicInteger;

public class User {
   private static AtomicInteger auto_ID = new AtomicInteger(0);
   private int id;
   private USER_TIER tier;
   public User(USER_TIER tier) {
      this.id = auto_ID.getAndIncrement();
      this.tier = tier;
   }
   public int getId(){
      return id;
   }
   public USER_TIER getTier(){
      return tier;
   }
}
