package appenders;

import models.LogMessage;

public interface IAppender {
   void appendMessage(LogMessage message);
}
