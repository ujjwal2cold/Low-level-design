package Services;

import Models.Expense;
import Models.Group;
import Models.User;
import Repositories.GroupRepo;

import java.util.Optional;

public class GroupService {
   private final GroupRepo repo;

   public GroupService(GroupRepo repo) {
      this.repo = repo;
   }

   int makeGroup(String name, User admin) {
      Group grp = new Group(name, admin);
      repo.addGroup(grp);
      return grp.getId();
   }

   boolean isValidGroup(int grpId) {
      return repo.findById(grpId).isPresent();
   }

   void addMember(int groupId, User newUser) {
      Optional<Group> grp = repo.findById(groupId);
      grp.ifPresent(group -> group.addUser(newUser));
   }

   void addExpense(int grpId, Expense e) {
      Optional<Group> grp = repo.findById(grpId);
      assert grp.isPresent();
      grp.get().addExpense(e);
   }
   public void showGroup(int groupId){
      Optional<Group> grp = repo.findById(groupId);
      grp.ifPresent(Group::getBalanceSheet);
   }
}

