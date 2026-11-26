package services;

import models.Order;
import models.User;
import repositories.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
   private final UserRepo uRepo;

   public UserService(UserRepo repo) {
      this.uRepo = repo;
   }

   public void createUser(String id, String name) {
      User u = new User(name, id);
      this.uRepo.addUser(u);
   }

   public long getHoldingsWorth(String userId) {
      Optional<User> u = uRepo.findById(userId);
      return u.map(User::getMarketValue)
              .orElseThrow(() -> new IllegalArgumentException("Such a user doesn't exist"));
   }

   public void printPortFolio(String userId) {
      Optional<User> u = uRepo.findById(userId);
      u.ifPresent(User::printPortFolio);
   }

   public List<Order> getOrderHistory(String userId) {
      Optional<User> u = uRepo.findById(userId);
      if (u.isPresent())
         return u.get().getOrderHistory();
      return new ArrayList<>();
   }

   public boolean exists(String userId) {
      return this.uRepo.findById(userId).isPresent();
   }

   public Optional<User> findById(String userId) {
      return this.uRepo.findById(userId);
   }
}
