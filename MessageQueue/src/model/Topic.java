package model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
public class Topic {
   private final int id;
   private final String name;
   private final List<ISubscriber> subscribers;
   private final List<String> messages;
   private final ReadWriteLock lock = new ReentrantReadWriteLock();

   public Topic(int id, String name) {
      this.id = id;
      this.name = name;
      this.subscribers = new CopyOnWriteArrayList<>();
      this.messages = new ArrayList<>();
   }

   void addMessage(String message) {
      lock.writeLock().lock();
      try {
         messages.add(message);
      } finally {
         lock.writeLock().unlock();
      }
   }

   public List<String> fetchMessages(int beginIndex) {
      lock.readLock().lock();
      try {
         if (beginIndex < 0 || beginIndex >= messages.size())
            return List.of();
         return List.copyOf(messages.subList(beginIndex, messages.size()));
      } finally {
         lock.readLock().unlock();
      }
   }

   public int getSize() {
      lock.readLock().lock();
      try {
         return messages.size();
      } finally {
         lock.readLock().unlock();
      }
   }

   public void addSubscriber(ISubscriber s) {
      subscribers.add(s);
   }
}
