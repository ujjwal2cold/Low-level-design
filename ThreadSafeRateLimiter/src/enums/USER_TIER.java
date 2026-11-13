package enums;

public enum USER_TIER {
   FREE_USER(0),
   PREMIUM_USER(5);
   private final int moreRequests;

   USER_TIER(int moreRequests) {
      this.moreRequests = moreRequests;
   }
   public int getMoreRequests() {
      return moreRequests;
   }
}
