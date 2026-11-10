import java.util.Optional;

class LibraryManagementService {
   private Library lib;
   private final IBookRepository repo;

   LibraryManagementService(Library lib, IBookRepository repo) {
      this.lib = lib;
      this.repo = repo;
   }

   void setBookFindingStrategy(IBookFindingStrategy bookFindingStrategy) {
      this.lib.setBookFindingStrategy(bookFindingStrategy);
   }

   void setBookAddingStrategy(IBookAddingStrategy bookAddingStrategy) {
      this.lib.setBookAddingStrategy(bookAddingStrategy);
   }

   Optional<Shelf> addBook(Book book) {
      Optional<Shelf> res = lib.addBook(book);
      if (res.isPresent()) {
         repo.addBook(book);
      }
      return res;
   }

   Optional<Book> findBook(int id) {
      return lib.findFreeBook(id);
   }

   void returnBook(int id) {
      lib.returnBook(repo.findById(id));
   }

   SubscribeResult subscribe(int bookId, Observer o) {
      return lib.subscribeBook(bookId, o);
   }
}
