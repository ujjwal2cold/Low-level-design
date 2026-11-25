package strategies;

import models.Book;
import models.Room;

import java.util.List;
import java.util.Optional;

public interface IBookFindingStrategy {
   Optional<Book> findBook(List<Room> rooms, int id);
}
