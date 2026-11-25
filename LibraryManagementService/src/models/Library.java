package models;

import enums.SubscribeResult;
import observer.Observer;
import strategies.IBookAddingStrategy;
import strategies.IBookFindingStrategy;

import java.util.List;
import java.util.Optional;

public class Library {

   private final List<Room> rooms;
   private volatile IBookFindingStrategy bookFindingStrategy;
   private volatile IBookAddingStrategy bookAddingStrategy;

   public Library(List<Room> rooms) {
      this.rooms = rooms;
   }

   public void setBookFindingStrategy(IBookFindingStrategy bookFindingStrategy) {
      this.bookFindingStrategy = bookFindingStrategy;
   }

   public void setBookAddingStrategy(IBookAddingStrategy bookAddingStrategy) {
      this.bookAddingStrategy = bookAddingStrategy;
   }

   public Optional<Shelf> addBook(Book book) {
      return this.bookAddingStrategy.addBook(this.rooms, book);
   }

   public void returnBook(Book book) {
      Shelf s = book.getShelf();
      if (s == null)
         throw new IllegalArgumentException("Book has no shelf");
      s.unbook(book);
   }

   public Optional<Book> findFreeBook(int id) {
      return this.bookFindingStrategy.findBook(this.rooms, id);
   }

   public SubscribeResult subscribeBook(int bookId, Observer o) {
      for (Room each : this.rooms) {
         SubscribeResult res = each.subscribeBook(bookId, o);
         if (res != SubscribeResult.NOT_FOUND)
            return res;
      }
      return SubscribeResult.NOT_FOUND;
   }

   public List<Room> getRooms() {
      return rooms;
   }
}
