import java.util.List;
import java.util.Optional;

interface IBookAddingStrategy {
   Optional<Shelf> addBook(List<Room> rooms, Book book);
}
