package observer;

import models.Book;

public interface Observer {
   void onBookAvailable(Book book);
}
