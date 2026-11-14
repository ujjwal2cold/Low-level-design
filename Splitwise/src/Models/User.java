package Models;

import java.util.Objects;

public class User {
   private final String id, name;
   public User(String id, String name){
      this.id = id;
      this.name = name;
   }
   public String getName(){
      return this.name;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return Objects.equals(id, user.id) && Objects.equals(name, user.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, name);
   }
}
