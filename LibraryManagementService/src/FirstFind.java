import java.util.List;
import java.util.Optional;

class FirstFind implements IBookFindingStrategy {
   @Override
   public Optional<Book> findBook(List<Room> rooms, int id) {
      for (Room each : rooms) {
         Optional<Book> res = each.tryAllot(id);
         if (res.isPresent()) {
            return res;
         }
      }
      return Optional.empty();
   }
}
