import appenders.ConsoleAppender;
import appenders.IAppender;
import enums.LogLevel;
import formatters.PlainTextFormatter;
import handlers.ErrorHandler;
import handlers.InfoHandler;
import handlers.LogHandler;
import handlers.WarnHandler;
import models.LogMessage;

import java.util.List;

public class Logger {
   private static final Logger INSTANCE = new Logger();
   private final LogHandler start;

   private Logger() {
      final IAppender appender = new ConsoleAppender(new PlainTextFormatter());
      final InfoHandler info = new InfoHandler();
      final WarnHandler warn = new WarnHandler();
      final ErrorHandler err = new ErrorHandler();
      info.setNextHandler(warn);
      warn.setNextHandler(err);
      for (LogHandler h : List.of(info, warn, err)) {
         h.addObserver(appender);
      }
      this.start = info;
   }

   public static Logger getInstance() {
      return INSTANCE;
   }

   private void log(LogLevel level, String message) {
      LogMessage msg = new LogMessage(level, message);
      start.tryHandling(msg);
   }

   public void info(String message) {
      log(LogLevel.INFO, message);
   }

   public void warn(String message) {
      log(LogLevel.WARN, message);
   }

   public void error(String message) {
      log(LogLevel.ERROR, message);
   }
}
