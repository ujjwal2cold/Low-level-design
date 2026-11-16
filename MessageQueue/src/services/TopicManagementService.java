package services;

import model.ISubscriber;
import model.Topic;
import model.TopicHandler;
import repositories.TopicHandlerRepository;
import repositories.TopicRepository;

import java.util.Optional;

public class TopicManagementService {
   TopicRepository topicRepository;
   TopicHandlerRepository topicHandlerRepository;

   public TopicManagementService(TopicRepository topicRepository, TopicHandlerRepository topicHandlerRepository) {
      this.topicRepository = topicRepository;
      this.topicHandlerRepository = topicHandlerRepository;
   }

   public void createTopic(int topicId, String topicName) {
      Topic t = new Topic(topicId, topicName);
      TopicHandler handler = new TopicHandler(t);
      this.topicRepository.addTopic(t);
      this.topicHandlerRepository.addTopicHandler(topicId, handler);
   }

   public void addSubscriberToTopic(int topicId, ISubscriber s) {
      Optional<Topic> t = topicRepository.findById(topicId);
      t.ifPresent(topic -> topic.addSubscriber(s));
   }

   public void publishMessage(int topicId, String message) {
      Optional<TopicHandler> handler = topicHandlerRepository.findById(topicId);
      handler.ifPresent(topicHandler -> topicHandler.publishMessage(message));
   }
}
