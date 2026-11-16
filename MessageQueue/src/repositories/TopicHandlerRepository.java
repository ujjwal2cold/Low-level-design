package repositories;

import model.TopicHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TopicHandlerRepository {
   private final Map<Integer, TopicHandler> topicHandlers = new ConcurrentHashMap<>();

   public void addTopicHandler(int topicHandlerId, TopicHandler topicHandler) {
      topicHandlers.put(topicHandlerId, topicHandler);
   }

   public Optional<TopicHandler> findById(int topicHandlerId) {
      return Optional.ofNullable(topicHandlers.get(topicHandlerId));
   }
}
