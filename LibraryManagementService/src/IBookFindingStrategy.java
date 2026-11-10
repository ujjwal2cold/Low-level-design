import java.util.List;
import java.util.Optional;

interface IBookFindingStrategy {
   Optional<Book> findBook(List<Room> rooms, int id);
}
