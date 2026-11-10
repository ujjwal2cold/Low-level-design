import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class BookDB implements IBookRepository {
   private final Map<Integer, Book> map = new ConcurrentHashMap<>();

   @Override
   public void addBook(Book book) {
      map.put(book.getId(), book);
   }

   @Override
   public Book findById(int id) {
      return map.get(id);
   }
}
