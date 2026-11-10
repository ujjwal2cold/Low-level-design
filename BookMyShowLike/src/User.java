public class User {
   private int id;
   private String name;

   User(int id, String name) {
      this.id = id;
      this.name = name;
   }

   private int getId() {
      return id;
   }

   private String getName() {
      return name;
   }
}
