package models;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class File implements Node {
   private final String name;
   private volatile StringBuilder content;
   private final ReentrantReadWriteLock rwLock;

   public File(String name) {
      this.name = name;
      this.content = new StringBuilder();
      this.rwLock = new ReentrantReadWriteLock();
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public int getSize() {
      rwLock.readLock().lock();
      try {
         return content.length();
      } finally {
         rwLock.readLock().unlock();
      }
   }

   @Override
   public boolean isFolder() {
      return false;
   }

   public void append(String s) {
      rwLock.writeLock().lock();
      try {
         content.append(s).append('\n');
      } finally {
         rwLock.writeLock().unlock();
      }
   }

   public String getData() {
      rwLock.readLock().lock();
      try {
         return new String(content);
      } finally {
         rwLock.readLock().unlock();
      }
   }
}
