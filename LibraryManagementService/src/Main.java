import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class Main {
   public static void main(String[] args) {
      Shelf s = new Shelf(1, 100);
      Book b0 = new Book("Atomic Habits");
      Book b1 = new Book("Alchemist");
      Book b2 = new Book("7 Habits of highly Effective People");
      Room r = new Room(1, List.of(s));
      Library lib = new Library(new ArrayList<>(List.of(r)));
      BookDB bookDB = new BookDB();
      LibraryManagementService lms = new LibraryManagementService(lib, bookDB);
      lms.setBookAddingStrategy(new FirstFit());
      lms.setBookFindingStrategy(new FirstFind());
      System.out.println(lms.findBook(b0.getId()));
      System.out.println(lms.findBook(b0.getId()));
      Optional<Shelf> s0 = lms.addBook(b0);
      System.out.println(s0);
      int bookId = b0.getId();
      Optional<Book> x = lms.findBook(bookId);
      System.out.println(x);
      Optional<Book> y = lms.findBook(bookId);
      Observer user = new Observer() {
         @Override
         public void onBookAvailable(Book book) {
            System.out.println("Book is available");
         }
      };
      if (y.isEmpty()) {
         SubscribeResult res = lms.subscribe(bookId, user);
         System.out.println(res);
      }
      lms.returnBook(bookId);
   }
}
