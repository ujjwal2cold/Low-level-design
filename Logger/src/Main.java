public class Main {
   public static void main(String[] args) {
      Thread t1 = new Thread(() -> Logger.getInstance().info("Info"));
      Thread t2 = new Thread(() -> Logger.getInstance().warn("Warn"));
      t1.start();
      t2.start();
   }
}
