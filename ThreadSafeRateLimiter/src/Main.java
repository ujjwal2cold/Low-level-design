import Models.User;
import Services.RateLimitingService;
import enums.ALGORITHMS;
import enums.USER_TIER;

public class Main {
   public static void main(String[] args) {
      User u = new User(USER_TIER.PREMIUM_USER);
      RateLimitingService service = new RateLimitingService(ALGORITHMS.TOKEN_BUCKET, 10, 10);
      Thread t1 = new Thread(() -> {
         service.allowRequest(u);
      });
      Thread t2 = new Thread(() -> {
         for (int i = 1; i <= 10; ++i) {
            boolean isAllowed = service.allowRequest(u);
            System.out.println("Request " + i + " is allowed = " + isAllowed);
         }
      });
      Thread t3 = new Thread(() -> {
         for (int i = 11; i < 21; i++) {
            boolean res = service.allowRequest(u);
            System.out.println("Request " + i + " is allowed = " + res);
         }
      });
      //t1.start();
      t2.start();
      t3.start();
   }
}
