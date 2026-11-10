interface IBookRepository {
   void addBook(Book book);

   Book findById(int id);
}
