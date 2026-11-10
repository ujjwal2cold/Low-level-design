import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

class Shelf {
   private int id;
   private int capacity;
   private List<Book> books;
   private ReentrantLock lock;

   Shelf(int id, int capacity) {
      this.id = id;
      this.capacity = capacity;
      books = new ArrayList<>();
      this.lock = new ReentrantLock();
   }

   private void addBook(Book book) {
      books.add(book);
   }

   Optional<Shelf> tryAddingBook(Book book) {
      lock.lock();
      try {
         if (books.size() >= capacity) {
            return Optional.empty();
         }
         if (!book.setShelf(this)) {
            return Optional.empty();
         }
         addBook(book);
         return Optional.of(this);
      } finally {
         lock.unlock();
      }
   }

   public void unbook(Book book) {
      Runnable task;
      lock.lock();
      try {
         if (book.getStatus() != STATUS.ALLOTTED) {
            throw new IllegalArgumentException("Book is not allotted");
         }
         task = book.unbook();
      } finally {
         lock.unlock();
      }
      task.run();
   }

   Optional<Book> tryAllot(int bookId) {
      lock.lock();
      try {
         for (Book each : books) {
            if (each.tryAllot(bookId)) {
               return Optional.of(each);
            }
         }
         return Optional.empty();
      } finally {
         lock.unlock();
      }
   }

   SubscribeResult subscribe(int bookId, Observer observer) {
      lock.lock();
      try {
         for (Book each : books) {
            if (each.getId() == bookId && each.getStatus() == STATUS.ALLOTTED) {
               each.addObserver(observer);
               return SubscribeResult.SUBSCRIBED;
            } else if (each.getId() == bookId && each.getStatus() == STATUS.AVAILABLE) {
               return SubscribeResult.AVAILABLE;
            }
         }
         return SubscribeResult.NOT_FOUND;
      } finally {
         lock.unlock();
      }
   }

   public int getId() {
      return id;
   }

   @Override
   public String toString() {
      return "Shelf{" +
              "id=" + id +
              ", capacity=" + capacity +
              '}';
   }
}
