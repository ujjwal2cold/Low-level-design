import models.File;
import models.Folder;
import services.FileManagerService;

import java.util.Optional;

public class Main {
   public static void main(String[] args) throws InterruptedException {
      FileManagerService svc = new FileManagerService();
      svc.addNode("/root/backend", new Folder("backend"));
      svc.addNode("/root/backend/java", new Folder("java"));
      svc.addNode("/root/backend/rust", new Folder("rust"));
      svc.addNode("/root/backend/java/", new File("Main.java"));
      Thread t1 = new Thread(() -> {
         for (int i = 1; i <= 20; ++i) {
            svc.appendToFile("/root/backend/java/Main.java", "Hello World");
         }
      });
      Thread t2 = new Thread(() -> {
         for (int i = 1; i <= 20; ++i) {
            svc.appendToFile("/root/backend/java/Main.java", "Hello World");
         }
      });
      t1.start();
      t2.start();
      t1.join();
      t2.join();
      Optional<String> out = svc.readFromFile("/root/backend/java/Main.java");
      out.ifPresent(x -> System.out.println(x));
   }
}
