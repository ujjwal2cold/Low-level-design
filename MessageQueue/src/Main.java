import model.ISubscriber;
import model.Subscriber;
import repositories.TopicHandlerRepository;
import repositories.TopicRepository;
import services.TopicManagementService;

class Main {
   public static void main(String[] args) {
      TopicHandlerRepository topicHandlerRepository = new TopicHandlerRepository();
      TopicRepository topicRepository = new TopicRepository();
      MessageQueue rabbitMQ = new MessageQueue(new TopicManagementService(topicRepository, topicHandlerRepository));
      ISubscriber s1 = new Subscriber(1), s2 = new Subscriber(2), s3 = new Subscriber(3);
      rabbitMQ.createTopic(1, "Order-Management");
      rabbitMQ.createTopic(2, "Payment Services");
      rabbitMQ.subscribe(1, s1);
      rabbitMQ.subscribe(1, s2);
      rabbitMQ.subscribe(2, s1);
      rabbitMQ.subscribe(2, s2);
      Thread t1 = new Thread(() -> {
         for (int i = 1; i < 20; ++i) {
            String message = "Message m" + i;
            rabbitMQ.publishMessage(message, 1);
         }
      });
      Thread t2 = new Thread(() -> {
         for (int i = 20; i < 40; ++i) {
            String message = "Message m" + i;
            rabbitMQ.publishMessage(message, 2);
         }
      });
      t1.start();
      t2.start();

   }
}