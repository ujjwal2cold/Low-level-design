package services;

import enums.SubscribeResult;
import models.Book;
import models.Library;
import models.Shelf;
import observer.Observer;
import repositories.IBookRepository;
import strategies.IBookAddingStrategy;
import strategies.IBookFindingStrategy;

import java.util.Optional;

public class LibraryManagementService {
   private Library lib;
   private final IBookRepository repo;

   public LibraryManagementService(Library lib, IBookRepository repo) {
      this.lib = lib;
      this.repo = repo;
   }

   public void setBookFindingStrategy(IBookFindingStrategy bookFindingStrategy) {
      this.lib.setBookFindingStrategy(bookFindingStrategy);
   }

   public void setBookAddingStrategy(IBookAddingStrategy bookAddingStrategy) {
      this.lib.setBookAddingStrategy(bookAddingStrategy);
   }

   public Optional<Shelf> addBook(Book book) {
      Optional<Shelf> res = lib.addBook(book);
      if (res.isPresent()) {
         repo.addBook(book);
      }
      return res;
   }

   public Optional<Book> findBook(int id) {
      return lib.findFreeBook(id);
   }

   public void returnBook(int id) {
      lib.returnBook(repo.findById(id));
   }

   public SubscribeResult subscribe(int bookId, Observer o) {
      return lib.subscribeBook(bookId, o);
   }
}
