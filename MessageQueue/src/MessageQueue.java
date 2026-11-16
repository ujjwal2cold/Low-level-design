import model.ISubscriber;
import services.TopicManagementService;

public class MessageQueue {
   TopicManagementService service;

   public MessageQueue(TopicManagementService service) {
      this.service = service;
   }

   public void createTopic(int topicId, String name) {
      service.createTopic(topicId, name);
   }

   public void subscribe(int topicId, ISubscriber subscriber) {
      service.addSubscriberToTopic(topicId, subscriber);
   }

   public void publishMessage(String message, int topicId) {
      service.publishMessage(topicId, message);
   }
}
