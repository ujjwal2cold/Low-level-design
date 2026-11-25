package repositories;

import models.Book;

public interface IBookRepository {
   void addBook(Book book);

   Book findById(int id);
}
