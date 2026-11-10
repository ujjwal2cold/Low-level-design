import java.util.List;
import java.util.Optional;

class FirstFit implements IBookAddingStrategy {
   @Override
   public Optional<Shelf> addBook(List<Room> rooms, Book book) {
      for (Room each : rooms) {
         Optional<Shelf> shelf = each.tryAddingBook(book);
         if (shelf.isPresent())
            return shelf;
      }
      return Optional.empty();
   }
}
