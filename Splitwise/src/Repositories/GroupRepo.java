package Repositories;

import Models.Group;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GroupRepo {
   private final Map<Integer, Group> groupRepo;

   public GroupRepo() {
      this.groupRepo = new HashMap<>();
   }

   public void addGroup(Group group) {
      this.groupRepo.put(group.getId(), group);
   }

   public Optional<Group> findById(int id) {
      return Optional.ofNullable(this.groupRepo.get(id));
   }
}
