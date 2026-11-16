package model;

public interface ISubscriber {
   int getId();
   void doWork(String message);
}
