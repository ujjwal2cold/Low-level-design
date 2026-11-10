import java.util.HashMap;
import java.util.Map;

public class ShowDB implements IShowRepository {
   private final Map<Integer, Show> map = new HashMap<>();

   @Override
   public void addShow(Show show) {
      map.put(show.getId(), show);
   }

   @Override
   public Show findShowById(int id) {
      return map.getOrDefault(id, null);
   }
}
