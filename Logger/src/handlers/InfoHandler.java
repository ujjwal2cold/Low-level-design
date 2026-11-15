package handlers;

import enums.LogLevel;
import models.LogMessage;

public class InfoHandler extends LogHandler {
   @Override
   boolean canHandle(LogMessage message) {
      return message.getLevel() == LogLevel.INFO;
   }
}
