package repositories;

import models.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepo {
   private final Map<String, User> map = new ConcurrentHashMap<>();

   public void addUser(User u) {
      map.put(u.getId(), u);
   }

   public Optional<User> findById(String userId) {
      return Optional.ofNullable(map.get(userId));
   }
}
