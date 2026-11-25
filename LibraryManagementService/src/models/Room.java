package models;

import enums.SubscribeResult;
import observer.Observer;

import java.util.List;
import java.util.Optional;

public class Room {
   private int id;
   private final List<Shelf> shelves;

   public Room(int id, List<Shelf> shelves) {
      this.id = id;
      this.shelves = List.copyOf(shelves);
   }

   public Optional<Shelf> hasShelf(Shelf s) {
      for (Shelf each : shelves) {
         if (each == s) {
            return Optional.of(each);
         }
      }
      return Optional.empty();
   }

   public Optional<Shelf> tryAddingBook(Book book) {
      for (Shelf shelf : shelves) {
         Optional<Shelf> res = shelf.tryAddingBook(book);
         if (res.isPresent()) {
            return res;
         }
      }
      return Optional.empty();
   }

   public Optional<Book> tryAllot(int bookId) {
      for (Shelf each : shelves) {
         Optional<Book> res = each.tryAllot(bookId);
         if (res.isPresent()) {
            return res;
         }
      }
      return Optional.empty();
   }

   public SubscribeResult subscribeBook(int bookId, Observer o) {
      for (Shelf each : shelves) {
         SubscribeResult res = each.subscribe(bookId, o);
         if (res != SubscribeResult.NOT_FOUND) {
            return res;
         }
      }
      return SubscribeResult.NOT_FOUND;
   }

   public int getId() {
      return id;
   }

   public List<Shelf> getShelves() {
      return shelves;
   }
}
