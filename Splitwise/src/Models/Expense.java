package Models;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Expense {
   private static AtomicInteger auto_id = new AtomicInteger(0);
   private int groupId;
   private int id;
   private double totalAmount;
   private User paidBy;
   private List<Member> involvedMembers;
   public Expense(int groupId, double amt, User paidBy, List<Member> involvedMembers){
      this.groupId = groupId;
      this.id = auto_id.getAndIncrement();
      this.totalAmount = amt;
      this.paidBy = paidBy;
      this.involvedMembers = involvedMembers;
   }
   public int getId(){
      return this.id;
   }
   public User getPaidBy(){
      return this.paidBy;
   }
   public List<Member> getInvolvedMembers(){
      return this.involvedMembers;
   }
   public double totalAmount(){
      return this.totalAmount;
   }
}
