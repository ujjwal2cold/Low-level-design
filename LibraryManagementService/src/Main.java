import enums.STATUS;
import enums.SubscribeResult;
import models.Book;
import models.Library;
import models.Room;
import models.Shelf;
import observer.Observer;
import repositories.BookDB;
import services.LibraryManagementService;
import strategies.FirstFind;
import strategies.FirstFit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test suite for the LibraryManagementService project.
 * Covers all organized packages and classes:
 *  1. models.Book
 *  2. models.Shelf
 *  3. models.Room
 *  4. models.Library
 *  5. repositories.BookDB
 *  6. strategies.FirstFit & strategies.FirstFind
 *  7. observer.Observer & enums.SubscribeResult (Publish-Subscribe notification)
 *  8. services.LibraryManagementService (End-to-End workflow)
 *  9. Concurrency & Thread-Safety Tests
 */
public class Main {
   private static int testsPassed = 0;
   private static int testsFailed = 0;

   public static void main(String[] args) throws InterruptedException {
      System.out.println("=================================================");
      System.out.println("   RUNNING LIBRARY MANAGEMENT SERVICE TESTS      ");
      System.out.println("=================================================\n");

      testBook();
      testShelf();
      testRoom();
      testBookDB();
      testStrategies();
      testObserverAndSubscription();
      testLibraryManagementServiceEndToEnd();
      testConcurrentAllotment();

      System.out.println("\n=================================================");
      System.out.printf("TEST RESULTS: %d Passed, %d Failed, %d Total\n",
              testsPassed, testsFailed, (testsPassed + testsFailed));
      System.out.println("=================================================");

      if (testsFailed > 0) {
         System.exit(1);
      }
   }

   // -------------------------------------------------------------
   // 1. Tests for Book
   // -------------------------------------------------------------
   private static void testBook() {
      System.out.println("[TEST GROUP] models.Book");

      Book b1 = new Book("Design Patterns");
      Book b2 = new Book("Clean Architecture");

      assertEquals("Design Patterns", b1.getName(), "Book name should match constructor argument");
      assertTrue(b2.getId() > b1.getId(), "Book ID should be auto-incrementing");
      assertEquals(STATUS.AVAILABLE, b1.getStatus(), "Initial book status should be AVAILABLE");

      // Test tryAllot with matching and non-matching IDs
      assertFalse(b1.tryAllot(b2.getId()), "tryAllot with wrong bookId should return false");
      assertEquals(STATUS.AVAILABLE, b1.getStatus(), "Status should remain AVAILABLE after failed allotment");

      assertTrue(b1.tryAllot(b1.getId()), "tryAllot with correct bookId should return true");
      assertEquals(STATUS.ALLOTTED, b1.getStatus(), "Status should be ALLOTTED after successful allotment");

      // Cannot re-allot an already allotted book
      assertFalse(b1.tryAllot(b1.getId()), "Allotting an already ALLOTTED book should return false");

      // Setting shelf
      Shelf s = new Shelf(1, 10);
      assertTrue(b1.setShelf(s), "Setting shelf for the first time should succeed");
      assertEquals(s, b1.getShelf(), "getShelf should return the assigned shelf");
      Shelf s2 = new Shelf(2, 10);
      assertFalse(b1.setShelf(s2), "Overwriting shelf on the same book should fail (atomic CAS)");
      assertEquals(s, b1.getShelf(), "Shelf should remain the first assigned shelf");

      // Unbook runnable
      Runnable notificationTask = b1.unbook();
      assertEquals(STATUS.AVAILABLE, b1.getStatus(), "Status should return to AVAILABLE after unbook()");
      assertTrue(notificationTask != null, "unbook() should return a non-null notification task");

      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 2. Tests for Shelf
   // -------------------------------------------------------------
   private static void testShelf() {
      System.out.println("[TEST GROUP] models.Shelf");

      Shelf shelf = new Shelf(101, 2); // capacity = 2
      assertEquals(101, shelf.getId(), "Shelf ID should match constructor argument");
      assertEquals(2, shelf.getCapacity(), "Shelf capacity should match constructor argument");

      Book bookA = new Book("Refactoring");
      Book bookB = new Book("Domain-Driven Design");
      Book bookC = new Book("Effective Java");

      // Adding books within capacity
      Optional<Shelf> resA = shelf.tryAddingBook(bookA);
      assertTrue(resA.isPresent(), "Adding book within capacity should succeed");
      assertEquals(shelf, resA.get(), "tryAddingBook should return Optional containing shelf");
      assertEquals(shelf, bookA.getShelf(), "Book's shelf reference should be set");

      Optional<Shelf> resB = shelf.tryAddingBook(bookB);
      assertTrue(resB.isPresent(), "Adding second book within capacity should succeed");

      // Exceeding capacity
      Optional<Shelf> resC = shelf.tryAddingBook(bookC);
      assertFalse(resC.isPresent(), "Adding third book beyond capacity should fail");
      assertTrue(bookC.getShelf() == null, "Rejected book should not have shelf set");

      // Cannot add a book that is already placed on a shelf
      Shelf anotherShelf = new Shelf(102, 5);
      Optional<Shelf> resDuplicate = anotherShelf.tryAddingBook(bookA);
      assertFalse(resDuplicate.isPresent(), "Book already on a shelf cannot be added to another shelf");

      // Try allot book from shelf
      Optional<Book> allotted = shelf.tryAllot(bookA.getId());
      assertTrue(allotted.isPresent(), "Should allot available book from shelf");
      assertEquals(bookA.getId(), allotted.get().getId(), "Allotted book ID should match requested ID");
      assertEquals(STATUS.ALLOTTED, bookA.getStatus(), "Book status should now be ALLOTTED");

      // Allot again should fail
      Optional<Book> secondAllot = shelf.tryAllot(bookA.getId());
      assertFalse(secondAllot.isPresent(), "Cannot allot already-allotted book");

      // Non-existent book ID
      Optional<Book> nonExistent = shelf.tryAllot(99999);
      assertFalse(nonExistent.isPresent(), "tryAllot should return empty for non-existent book ID");

      // Unbook from shelf
      shelf.unbook(bookA);
      assertEquals(STATUS.AVAILABLE, bookA.getStatus(), "Book should be AVAILABLE after unbook on shelf");

      // Unbook on an already available book should throw IllegalArgumentException
      boolean threwException = false;
      try {
         shelf.unbook(bookA);
      } catch (IllegalArgumentException e) {
         threwException = true;
      }
      assertTrue(threwException, "unbook on non-allotted book should throw IllegalArgumentException");

      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 3. Tests for Room
   // -------------------------------------------------------------
   private static void testRoom() {
      System.out.println("[TEST GROUP] models.Room");

      Shelf s1 = new Shelf(1, 1);
      Shelf s2 = new Shelf(2, 1);
      Room room = new Room(10, List.of(s1, s2));

      assertEquals(10, room.getId(), "Room ID should match constructor argument");
      assertEquals(2, room.getShelves().size(), "Room should contain 2 shelves");

      assertTrue(room.hasShelf(s1).isPresent(), "room.hasShelf should find s1");
      Shelf sExternal = new Shelf(99, 1);
      assertFalse(room.hasShelf(sExternal).isPresent(), "room.hasShelf should return empty for external shelf");

      Book b1 = new Book("Book 1");
      Book b2 = new Book("Book 2");
      Book b3 = new Book("Book 3");

      // tryAddingBook cascades to shelves
      Optional<Shelf> added1 = room.tryAddingBook(b1);
      assertTrue(added1.isPresent() && added1.get() == s1, "First book should be added to s1");

      Optional<Shelf> added2 = room.tryAddingBook(b2);
      assertTrue(added2.isPresent() && added2.get() == s2, "Second book should be added to s2");

      Optional<Shelf> added3 = room.tryAddingBook(b3);
      assertFalse(added3.isPresent(), "Room should return empty when all its shelves are full");

      // tryAllot cascades to shelves
      Optional<Book> found = room.tryAllot(b2.getId());
      assertTrue(found.isPresent() && found.get().getId() == b2.getId(), "tryAllot in room should find and allot b2");

      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 4. Tests for BookDB (repositories)
   // -------------------------------------------------------------
   private static void testBookDB() {
      System.out.println("[TEST GROUP] repositories.BookDB");

      BookDB db = new BookDB();
      Book b = new Book("Database Internals");

      assertTrue(db.findById(b.getId()) == null, "Querying unregistered book should return null");

      db.addBook(b);
      Book retrieved = db.findById(b.getId());
      assertEquals(b, retrieved, "Retrieved book from BookDB should match added book");

      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 5. Tests for Strategies (FirstFit & FirstFind)
   // -------------------------------------------------------------
   private static void testStrategies() {
      System.out.println("[TEST GROUP] strategies.FirstFit & strategies.FirstFind");

      Shelf s1 = new Shelf(1, 1);
      Shelf s2 = new Shelf(2, 1);
      Room r1 = new Room(1, List.of(s1));
      Room r2 = new Room(2, List.of(s2));
      List<Room> rooms = List.of(r1, r2);

      FirstFit fitStrategy = new FirstFit();
      FirstFind findStrategy = new FirstFind();

      Book book1 = new Book("Strategy Book 1");
      Book book2 = new Book("Strategy Book 2");
      Book book3 = new Book("Strategy Book 3");

      // FirstFit should place book1 into Room 1 (s1)
      Optional<Shelf> placed1 = fitStrategy.addBook(rooms, book1);
      assertTrue(placed1.isPresent() && placed1.get() == s1, "FirstFit should place book1 into Room 1's shelf");

      // FirstFit should place book2 into Room 2 (s2)
      Optional<Shelf> placed2 = fitStrategy.addBook(rooms, book2);
      assertTrue(placed2.isPresent() && placed2.get() == s2, "FirstFit should place book2 into Room 2's shelf");

      // FirstFit fails when full
      Optional<Shelf> placed3 = fitStrategy.addBook(rooms, book3);
      assertFalse(placed3.isPresent(), "FirstFit should return empty when all rooms are full");

      // FirstFind finds and allots book2 from Room 2
      Optional<Book> found2 = findStrategy.findBook(rooms, book2.getId());
      assertTrue(found2.isPresent() && found2.get().getId() == book2.getId(), "FirstFind should find and allot book2");
      assertEquals(STATUS.ALLOTTED, book2.getStatus(), "Book 2 status should be ALLOTTED");

      // FirstFind fails on already allotted book
      Optional<Book> foundAgain = findStrategy.findBook(rooms, book2.getId());
      assertFalse(foundAgain.isPresent(), "FirstFind should return empty for already allotted book");

      // FirstFind fails on non-existent ID
      Optional<Book> notFound = findStrategy.findBook(rooms, 8888);
      assertFalse(notFound.isPresent(), "FirstFind should return empty for non-existent book");

      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 6. Tests for Observer and Subscription Notification
   // -------------------------------------------------------------
   private static void testObserverAndSubscription() {
      System.out.println("[TEST GROUP] observer.Observer & Subscription Notification");

      Shelf shelf = new Shelf(1, 5);
      Room room = new Room(1, List.of(shelf));
      Library library = new Library(List.of(room));

      Book book = new Book("Clean Code");
      shelf.tryAddingBook(book);

      AtomicInteger user1Notifications = new AtomicInteger(0);
      AtomicInteger user2Notifications = new AtomicInteger(0);

      Observer user1 = b -> user1Notifications.incrementAndGet();
      Observer user2 = b -> user2Notifications.incrementAndGet();

      // Case 1: Subscribe to book when it is AVAILABLE -> Returns AVAILABLE (no wait needed)
      SubscribeResult resAvailable = library.subscribeBook(book.getId(), user1);
      assertEquals(SubscribeResult.AVAILABLE, resAvailable, "Subscribing to available book should return AVAILABLE");

      // Case 2: Subscribe to non-existent book -> Returns NOT_FOUND
      SubscribeResult resNotFound = library.subscribeBook(99999, user1);
      assertEquals(SubscribeResult.NOT_FOUND, resNotFound, "Subscribing to non-existent book should return NOT_FOUND");

      // Allot the book so it becomes ALLOTTED
      shelf.tryAllot(book.getId());
      assertEquals(STATUS.ALLOTTED, book.getStatus(), "Book is now ALLOTTED");

      // Case 3: Subscribe to ALLOTTED book -> Returns SUBSCRIBED
      SubscribeResult resSub1 = library.subscribeBook(book.getId(), user1);
      SubscribeResult resSub2 = library.subscribeBook(book.getId(), user2);
      assertEquals(SubscribeResult.SUBSCRIBED, resSub1, "First user subscription should return SUBSCRIBED");
      assertEquals(SubscribeResult.SUBSCRIBED, resSub2, "Second user subscription should return SUBSCRIBED");

      // Neither user notified yet
      assertEquals(0, user1Notifications.get(), "User 1 should not have received notification yet");
      assertEquals(0, user2Notifications.get(), "User 2 should not have received notification yet");

      // Now book is returned -> triggers unbook notification task
      library.returnBook(book);

      // Both users should receive the notification callback
      assertEquals(1, user1Notifications.get(), "User 1 should be notified once on book return");
      assertEquals(1, user2Notifications.get(), "User 2 should be notified once on book return");
      assertEquals(STATUS.AVAILABLE, book.getStatus(), "Book should be AVAILABLE after return");

      // Subsequent return without re-allotting should throw exception
      boolean threwException = false;
      try {
         library.returnBook(book);
      } catch (IllegalArgumentException e) {
         threwException = true;
      }
      assertTrue(threwException, "Returning available book should throw IllegalArgumentException");

      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 7. Tests for LibraryManagementService End-to-End
   // -------------------------------------------------------------
   private static void testLibraryManagementServiceEndToEnd() {
      System.out.println("[TEST GROUP] services.LibraryManagementService End-to-End");

      Shelf s1 = new Shelf(1, 2);
      Shelf s2 = new Shelf(2, 2);
      Room r1 = new Room(1, List.of(s1));
      Room r2 = new Room(2, List.of(s2));
      Library lib = new Library(new ArrayList<>(List.of(r1, r2)));
      BookDB bookDB = new BookDB();

      LibraryManagementService lms = new LibraryManagementService(lib, bookDB);
      lms.setBookAddingStrategy(new FirstFit());
      lms.setBookFindingStrategy(new FirstFind());

      Book b1 = new Book("Atomic Habits");
      Book b2 = new Book("The Alchemist");

      // Add books through service
      Optional<Shelf> added1 = lms.addBook(b1);
      assertTrue(added1.isPresent(), "addBook for b1 should succeed");
      assertEquals(b1, bookDB.findById(b1.getId()), "b1 should be stored in BookDB repository");

      Optional<Shelf> added2 = lms.addBook(b2);
      assertTrue(added2.isPresent(), "addBook for b2 should succeed");

      // Find and borrow b1
      Optional<Book> borrowed1 = lms.findBook(b1.getId());
      assertTrue(borrowed1.isPresent(), "lms.findBook should find and allot b1");
      assertEquals(STATUS.ALLOTTED, b1.getStatus(), "b1 status should be ALLOTTED");

      // Next attempt to borrow b1 should fail
      Optional<Book> borrowedAgain = lms.findBook(b1.getId());
      assertFalse(borrowedAgain.isPresent(), "lms.findBook should return empty when b1 is already allotted");

      // User subscribes to b1
      AtomicInteger notifiedCount = new AtomicInteger(0);
      SubscribeResult subResult = lms.subscribe(b1.getId(), book -> notifiedCount.incrementAndGet());
      assertEquals(SubscribeResult.SUBSCRIBED, subResult, "Subscription to allotted b1 should be SUBSCRIBED");

      // Return b1 using ID
      lms.returnBook(b1.getId());
      assertEquals(1, notifiedCount.get(), "Subscriber should be notified upon book return");
      assertEquals(STATUS.AVAILABLE, b1.getStatus(), "b1 should be AVAILABLE after return");

      // Now b1 can be borrowed again
      Optional<Book> borrowedReallot = lms.findBook(b1.getId());
      assertTrue(borrowedReallot.isPresent(), "b1 should be borrowable again after return");

      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 8. Tests for Concurrency & Thread-Safety
   // -------------------------------------------------------------
   private static void testConcurrentAllotment() throws InterruptedException {
      System.out.println("[TEST GROUP] Concurrency: Thread-Safe Book Allotment");

      Shelf shelf = new Shelf(1, 10);
      Book sharedBook = new Book("Concurrent Programming in Java");
      shelf.tryAddingBook(sharedBook);

      int threadCount = 12;
      ExecutorService executor = Executors.newFixedThreadPool(threadCount);
      CountDownLatch readyLatch = new CountDownLatch(threadCount);
      CountDownLatch startLatch = new CountDownLatch(1);
      CountDownLatch doneLatch = new CountDownLatch(threadCount);

      AtomicInteger successfulAllotments = new AtomicInteger(0);
      AtomicInteger failedAllotments = new AtomicInteger(0);

      for (int i = 0; i < threadCount; i++) {
         executor.submit(() -> {
            readyLatch.countDown();
            try {
               startLatch.await(); // Synchronize all threads to fire simultaneously
               Optional<Book> res = shelf.tryAllot(sharedBook.getId());
               if (res.isPresent()) {
                  successfulAllotments.incrementAndGet();
               } else {
                  failedAllotments.incrementAndGet();
               }
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            } finally {
               doneLatch.countDown();
            }
         });
      }

      readyLatch.await();
      startLatch.countDown(); // Fire!
      doneLatch.await();
      executor.shutdown();

      assertEquals(1, successfulAllotments.get(), "Exactly 1 thread should successfully allot the book");
      assertEquals(threadCount - 1, failedAllotments.get(), "Remaining (threadCount - 1) threads should fail to allot");
      assertEquals(STATUS.ALLOTTED, sharedBook.getStatus(), "Shared book status should remain ALLOTTED");

      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // Helper assertion methods
   // -------------------------------------------------------------
   private static void assertTrue(boolean condition, String description) {
      if (condition) {
         System.out.println("  [PASS] " + description);
         testsPassed++;
      } else {
         System.err.println("  [FAIL] " + description);
         testsFailed++;
      }
   }

   private static void assertFalse(boolean condition, String description) {
      assertTrue(!condition, description);
   }

   private static void assertEquals(Object expected, Object actual, String description) {
      boolean equals = (expected == null && actual == null) || (expected != null && expected.equals(actual));
      if (equals) {
         System.out.println("  [PASS] " + description);
         testsPassed++;
      } else {
         System.err.println("  [FAIL] " + description + " (Expected: " + expected + ", Actual: " + actual + ")");
         testsFailed++;
      }
   }
}
