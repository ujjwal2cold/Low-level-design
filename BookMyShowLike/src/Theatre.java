import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Theatre {
   private static final AtomicInteger autoID = new AtomicInteger(0);
   private int id;
   private String name;
   private String address;
   private List<Screen> allScreens;

   Theatre(String name, String address, List<Screen> allScreens) {
      this.id = autoID.getAndIncrement();
      this.name = name;
      this.address = address;
      this.allScreens = List.copyOf(allScreens);
   }

   public int getId() {
      return this.id;
   }

   public boolean tryAddingShow(Show show) {
      for (Screen each : allScreens) {
         if (each.getId() == show.getScreenId()) {
            if (each.setShow(show))
               return true;
         }
      }
      return false;
   }
}
