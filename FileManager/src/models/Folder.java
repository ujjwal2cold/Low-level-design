package models;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Folder implements Node {
   private final Map<String, Node> children;
   private String name;
   private ReentrantReadWriteLock rwLock;

   public Folder(String name) {
      this.name = name;
      this.children = new ConcurrentHashMap<>();
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
         int res = 0;
         for (Node n : children.values()) {
            res += n.getSize();
         }
         return res;
      } finally {
         rwLock.readLock().unlock();
      }
   }

   @Override
   public boolean isFolder() {
      return true;
   }

   public void addNode(Node newNode) {
      rwLock.writeLock().lock();
      try {
         children.put(newNode.getName(), newNode);
      } finally {
         rwLock.writeLock().unlock();
      }
   }

   public void appendToFile(String fileName, String message) {
      Node child = children.get(fileName);
      if (child != null && !child.isFolder()) {
         ((File) child).append(message);
      }
   }

   public Optional<String> readFromFile(String fileName) {
      Node child = children.get(fileName);
      if (child != null && !child.isFolder()) {
         return Optional.of(((File) child).getData());
      }
      return Optional.empty();
   }
}
