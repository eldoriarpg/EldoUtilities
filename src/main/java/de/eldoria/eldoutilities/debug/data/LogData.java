package de.eldoria.eldoutilities.debug.data;

public class LogData {
    private final String log;
    private final String[] internalExceptions;
    private final String[] exceptions;

    public LogData(String log, String[] internalExceptions, String[] exceptions) {
        this.log = log;
        this.internalExceptions = internalExceptions;
        this.exceptions = exceptions;
    }
}
