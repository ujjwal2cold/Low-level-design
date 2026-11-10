import java.util.List;
import java.util.Optional;

class Library {

   private final List<Room> rooms;
   private volatile IBookFindingStrategy bookFindingStrategy;
   private volatile IBookAddingStrategy bookAddingStrategy;

   Library(List<Room> rooms) {
      this.rooms = rooms;
   }

   void setBookFindingStrategy(IBookFindingStrategy bookFindingStrategy) {
      this.bookFindingStrategy = bookFindingStrategy;
   }

   void setBookAddingStrategy(IBookAddingStrategy bookAddingStrategy) {
      this.bookAddingStrategy = bookAddingStrategy;
   }

   Optional<Shelf> addBook(Book book) {
      return this.bookAddingStrategy.addBook(this.rooms, book);
   }

   void returnBook(Book book) {
      Shelf s = book.getShelf();
      if (s == null)
         throw new IllegalArgumentException("Book has no shelf");
      s.unbook(book);

   }

   Optional<Book> findFreeBook(int id) {
      return this.bookFindingStrategy.findBook(this.rooms, id);
   }

   SubscribeResult subscribeBook(int bookId, Observer o) {
      for (Room each : this.rooms) {
         SubscribeResult res = each.subscribeBook(bookId, o);
         if (res != SubscribeResult.NOT_FOUND)
            return res;
      }
      return SubscribeResult.NOT_FOUND;
   }
}
