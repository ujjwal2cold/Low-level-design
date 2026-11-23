package services;

import models.Folder;
import models.Node;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FileManagerService {
   private Folder root;
   private Map<String, Folder> allFolders;

   public FileManagerService() {
      this.root = new Folder("root");
      this.allFolders = new ConcurrentHashMap<>();
      allFolders.put("root", root);
   }

   private Optional<Folder> resolveFolder(String path) {
      int lastSlash = path.lastIndexOf('/');
      if (lastSlash == -1)
         return Optional.empty();
      String subPath = path.substring(0, lastSlash);
      int secondLastSlash = subPath.lastIndexOf('/');
      String folderName = path.substring(secondLastSlash + 1, lastSlash);
      Folder folder = allFolders.getOrDefault(folderName, null);
      return folder == null ? Optional.empty() : Optional.of(folder);
   }

   public boolean addNode(String path, Node node) {
      Optional<Folder> latest = resolveFolder(path);
      if (latest.isEmpty())
         return false;
      Folder parent = latest.get();
      parent.addNode(node);
      if (node.isFolder())
         allFolders.put(node.getName(), (Folder) node);
      return true;
   }

   public void appendToFile(String path, String message) {
      Optional<Folder> latest = resolveFolder(path);
      if (latest.isPresent()) {
         Folder parent = latest.get();
         int lastSlash = path.lastIndexOf('/');
         String fileName = path.substring(lastSlash + 1);
         parent.appendToFile(fileName, message);
      }
   }

   public Optional<String> readFromFile(String path) {
      Optional<Folder> latest = resolveFolder(path);
      if (latest.isPresent()) {
         Folder parent = latest.get();
         int lastSlash = path.lastIndexOf('/');
         String fileName = path.substring(lastSlash + 1);
         return parent.readFromFile(fileName);
      }
      return Optional.empty();
   }
}
