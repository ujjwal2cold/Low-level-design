package handlers;

import appenders.IAppender;
import models.LogMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class LogHandler {
   protected LogHandler nextHandler;
   protected List<IAppender> observers;

   LogHandler() {
      this.observers = new CopyOnWriteArrayList<>();
   }

   public void addObserver(IAppender observer) {
      this.observers.add(observer);
   }

   public void setNextHandler(LogHandler nextHandler) {
      this.nextHandler = nextHandler;
   }

   public void notifyObservers(LogMessage message) {
      for (IAppender appender : observers) {
         appender.appendMessage(message);
      }
   }

   public void tryHandling(LogMessage message) {
      if (canHandle(message))
         notifyObservers(message);
      else if (nextHandler != null)
         nextHandler.tryHandling(message);
   }

   abstract boolean canHandle(LogMessage message);
}
