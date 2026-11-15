package appenders;

import formatters.IFormatter;
import models.LogMessage;

public class ConsoleAppender implements IAppender {
   private final IFormatter formatter;

   public ConsoleAppender(IFormatter formatter) {
      this.formatter = formatter;
   }

   @Override
   public synchronized void appendMessage(LogMessage message) {
      System.out.println(formatter.formatMessage(message));
   }
}
