package logging;

public enum LogLevel {
	DEBUG,
	WARNING,
	SEVERE,

	// here to override logs in configuration, should not be used when logging
	NONE,
}
