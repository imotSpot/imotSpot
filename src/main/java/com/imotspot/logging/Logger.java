package com.imotspot.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.slf4j.Marker;
import org.slf4j.event.LoggingEvent;

import java.io.File;
import java.util.Iterator;

public class Logger implements org.slf4j.Logger {

    ch.qos.logback.classic.Logger wrappedLogger = null;

    public static final File LOG_DIR = new File("log");

    static {
        LOG_DIR.mkdirs();
    }

    public Logger(org.slf4j.Logger wrappedLogger) {
        this.wrappedLogger = (ch.qos.logback.classic.Logger) wrappedLogger;
    }

    public Level getEffectiveLevel() {
        return wrappedLogger.getEffectiveLevel();
    }

    public Level getLevel() {
        return wrappedLogger.getLevel();
    }

    @Override
    public String getName() {
        return wrappedLogger.getName();
    }

    public void setLevel(Level newLevel) {
        wrappedLogger.setLevel(newLevel);
    }

    public void detachAndStopAllAppenders() {
        wrappedLogger.detachAndStopAllAppenders();
    }

    public boolean detachAppender(String name) {
        return wrappedLogger.detachAppender(name);
    }

    public void addAppender(Appender<ILoggingEvent> newAppender) {
        wrappedLogger.addAppender(newAppender);
    }

    public boolean isAttached(Appender<ILoggingEvent> appender) {
        return wrappedLogger.isAttached(appender);
    }

    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        return wrappedLogger.iteratorForAppenders();
    }

    public Appender<ILoggingEvent> getAppender(String name) {
        return wrappedLogger.getAppender(name);
    }

    public void callAppenders(ILoggingEvent event) {
        wrappedLogger.callAppenders(event);
    }

    public boolean detachAppender(Appender<ILoggingEvent> appender) {
        return wrappedLogger.detachAppender(appender);
    }

    @Override
    public void trace(String msg) {
        wrappedLogger.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        wrappedLogger.trace(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        wrappedLogger.trace(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... argArray) {
        wrappedLogger.trace(format, argArray);
    }

    @Override
    public void trace(String msg, Throwable t) {
        wrappedLogger.trace(msg, t);
    }

    @Override
    public void trace(Marker marker, String msg) {
        wrappedLogger.trace(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        wrappedLogger.trace(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        wrappedLogger.trace(marker, format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        wrappedLogger.trace(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return wrappedLogger.isDebugEnabled();
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return wrappedLogger.isDebugEnabled(marker);
    }

    @Override
    public void debug(String msg) {
        wrappedLogger.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        wrappedLogger.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        wrappedLogger.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... argArray) {
        wrappedLogger.debug(format, argArray);
    }

    @Override
    public void debug(String msg, Throwable t) {
        wrappedLogger.debug(msg, t);
    }

    @Override
    public void debug(Marker marker, String msg) {
        wrappedLogger.debug(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        wrappedLogger.debug(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        wrappedLogger.debug(marker, format, argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        wrappedLogger.debug(marker, msg, t);
    }

    @Override
    public void error(String msg) {
        wrappedLogger.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        wrappedLogger.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        wrappedLogger.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... argArray) {
        wrappedLogger.error(format, argArray);
    }

    @Override
    public void error(String msg, Throwable t) {
        wrappedLogger.error(msg, t);
    }

    @Override
    public void error(Marker marker, String msg) {
        wrappedLogger.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        wrappedLogger.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        wrappedLogger.error(marker, format, argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        wrappedLogger.error(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return wrappedLogger.isInfoEnabled();
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return wrappedLogger.isInfoEnabled(marker);
    }

    @Override
    public void info(String msg) {
        wrappedLogger.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        wrappedLogger.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        wrappedLogger.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... argArray) {
        wrappedLogger.info(format, argArray);
    }

    @Override
    public void info(String msg, Throwable t) {
        wrappedLogger.info(msg, t);
    }

    @Override
    public void info(Marker marker, String msg) {
        wrappedLogger.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        wrappedLogger.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        wrappedLogger.info(marker, format, argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        wrappedLogger.info(marker, msg, t);
    }

    @Override
    public boolean isTraceEnabled() {
        return wrappedLogger.isTraceEnabled();
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return wrappedLogger.isTraceEnabled(marker);
    }

    @Override
    public boolean isErrorEnabled() {
        return wrappedLogger.isErrorEnabled();
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return wrappedLogger.isErrorEnabled(marker);
    }

    @Override
    public boolean isWarnEnabled() {
        return wrappedLogger.isWarnEnabled();
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return wrappedLogger.isWarnEnabled(marker);
    }

    public boolean isEnabledFor(Marker marker, Level level) {
        return wrappedLogger.isEnabledFor(marker, level);
    }

    public boolean isEnabledFor(Level level) {
        return wrappedLogger.isEnabledFor(level);
    }

    @Override
    public void warn(String msg) {
        wrappedLogger.warn(msg);
    }

    @Override
    public void warn(String msg, Throwable t) {
        wrappedLogger.warn(msg, t);
    }

    @Override
    public void warn(String format, Object arg) {
        wrappedLogger.warn(format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        wrappedLogger.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... argArray) {
        wrappedLogger.warn(format, argArray);
    }

    @Override
    public void warn(Marker marker, String msg) {
        wrappedLogger.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        wrappedLogger.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        wrappedLogger.warn(marker, format, argArray);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        wrappedLogger.warn(marker, msg, t);
    }

    public boolean isAdditive() {
        return wrappedLogger.isAdditive();
    }

    public void setAdditive(boolean additive) {
        wrappedLogger.setAdditive(additive);
    }

    @Override
    public String toString() {
        return wrappedLogger.toString();
    }

    public LoggerContext getLoggerContext() {
        return wrappedLogger.getLoggerContext();
    }

    public void log(Marker marker, String fqcn, int levelInt, String message, Object[] argArray, Throwable t) {
        wrappedLogger.log(marker, fqcn, levelInt, message, argArray, t);
    }

    public void log(LoggingEvent slf4jEvent) {
        wrappedLogger.log(slf4jEvent);
    }
}