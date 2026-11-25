package strategies;

import models.Book;
import models.Room;
import models.Shelf;

import java.util.List;
import java.util.Optional;

public interface IBookAddingStrategy {
   Optional<Shelf> addBook(List<Room> rooms, Book book);
}
