package handlers;

import enums.LogLevel;
import models.LogMessage;

public class ErrorHandler extends LogHandler {
   @Override
   boolean canHandle(LogMessage message) {
      return message.getLevel() == LogLevel.ERROR;
   }
}
