package logging;

import java.util.ArrayList;
import java.util.List;

public class Logger {

	public static Logger get(LogType type) {
		return loggers[type.ordinal()];
	}

	public void log(LogLevel level, String s) {
		if (level.compareTo(this.minLevel) >= 0) {
			this.data.add(new LogEntry(level, s));
		}
	}

	// returns the highest log level encountered within the logs.
	// If there are no logs, DEBUG is returned.
	public LogLevel dump() {
		LogLevel maxLevel = LogLevel.DEBUG;
		for (LogEntry entry : this.data) {
			if (entry.level.compareTo(maxLevel) > 0) {
				maxLevel = entry.level;
			}
			System.out.println("[" + entry.level.name() + "] " + entry.data);
		}

		return maxLevel;
	}

	/////////////////////////

	LogLevel minLevel;
	List<LogEntry> data;

	private Logger(LogLevel minLevel) {
		this.data = new ArrayList<>();
		this.minLevel = minLevel;
	}

	private static Logger[] loggers = initLoggers();

	private static Logger[] initLoggers() {
		Logger[] loggers = new Logger[LogType.values().length];
		// if we want to print different levels of logs for each stage, change the parameter in the constructor
		loggers[LogType.LEXER.ordinal()] = new Logger(LogLevel.DEBUG);
		loggers[LogType.PARSER.ordinal()] = new Logger(LogLevel.DEBUG);
		loggers[LogType.VERIFIER.ordinal()] = new Logger(LogLevel.DEBUG);
		return loggers;
	}

	private static class LogEntry {
		LogLevel level;
		String data;

		public LogEntry(LogLevel level, String data) {
			this.level = level;
			this.data = data;
		}
	}
}
