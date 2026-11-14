package Models;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Group {
   private static final AtomicInteger auto_id = new AtomicInteger(0);
   private final int id;
   private final String name;
   private final List<User> users;
   private final Map<User, BalanceSheet> map;
   private final ReentrantLock lock;

   public Group(String name, User u) {
      this.id = auto_id.getAndIncrement();
      this.name = name;
      this.users = new ArrayList<>(List.of(u));
      this.map = new HashMap<>();
      this.lock = new ReentrantLock();
   }

   public int getId() {
      return this.id;
   }

   public void addUser(User newUser) {
      lock.lock();
      try {
         users.add(newUser);
      } finally {
         lock.unlock();
      }
   }

   public void addExpense(Expense e) {
      lock.lock();
      try {
         User paidBy = e.getPaidBy();
         List<Member> members = e.getInvolvedMembers();
         BalanceSheet sheet = map.computeIfAbsent(paidBy, k -> new BalanceSheet());
         sheet.addTotal(e.totalAmount());
         for (Member each : members) {
            User u = each.getUser();
            BalanceSheet other = map.computeIfAbsent(u, k -> new BalanceSheet());
            if (u == paidBy)
               continue;
            double amt = each.getAmt();
            sheet.creditFrom(u, amt);
            other.debitFrom(paidBy, amt);
         }
      } finally {
         lock.unlock();
      }
   }

   public void getBalanceSheet() {
      lock.lock();
      try {
         for (User each : users) {
            BalanceSheet sheet = map.computeIfAbsent(each, k -> new BalanceSheet());
            System.out.println("User : " + each.getName() + " has paid total amount : " + sheet.getTotalAmountPaid());
            System.out.println(sheet.describeBalance());
         }
      } finally {
         lock.unlock();
      }
   }
}
