import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class Book {
   private static final AtomicInteger auto_id = new AtomicInteger(0);
   private final Queue<Observer> waitingQueue = new ArrayDeque<>();
   private int id;
   private final AtomicReference<Shelf> shelf = new AtomicReference<>(null);
   private String name;
   private volatile STATUS status;

   Book(String name) {
      this.id = auto_id.getAndIncrement();
      this.name = name;
      this.status = STATUS.AVAILABLE;
   }
   void addObserver(Observer o){
      waitingQueue.add(o);
   }

   public boolean setShelf(Shelf shelf) {
      return this.shelf.compareAndSet(null, shelf);
   }

   public int getId() {
      return this.id;
   }

   boolean tryAllot(int bookId) {
      boolean res = this.id == bookId && this.status == STATUS.AVAILABLE;
      if (res) {
         this.status = STATUS.ALLOTTED;
      }
      return res;
   }

   Shelf getShelf() {
      return this.shelf.get();
   }

   Runnable unbook() {
      List<Observer> allObservers = new ArrayList<>(waitingQueue);
      waitingQueue.clear();
      this.status = STATUS.AVAILABLE;
      return new Runnable() {
         @Override
         public void run() {
            for(Observer o : allObservers) {
               o.onBookAvailable(Book.this);
            }
         }
      };
   }

   STATUS getStatus() {
      return this.status;
   }

   @Override
   public String toString() {
      return "Book{" +
              "id=" + id +
              ", shelf=" + shelf +
              ", name='" + name + '\'' +
              ", status=" + status +
              '}';
   }
}
