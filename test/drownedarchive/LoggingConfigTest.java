package drownedarchive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.Test;

/* Tests the real log4j2.xml, since a broken config fails silently while the game carries on. */
class LoggingConfigTest {
    private static final Logger LOG = LogManager.getLogger(LoggingConfigTest.class);

    private Configuration config() {
        return ((LoggerContext) LogManager.getContext(false)).getConfiguration();
    }

    @Test
    void fileAppenderIsConfiguredForTheGameLog() {
        Appender appender = config().getAppender("GameLog");
        assertTrue(appender instanceof FileAppender, "expected a FileAppender but found " + appender);
        assertEquals("logs/drowned-archive.log", ((FileAppender) appender).getFileName());
    }

    @Test
    void loggedMessageIsWrittenToTheFlatFile() throws IOException {
        String written = TestLog.writtenDuring(() -> LOG.info("a message for the flat file"));
        assertTrue(written.contains("a message for the flat file"), written);
    }

    @Test
    void noConsoleAppenderIsAttached() {
        for (Appender appender : config().getAppenders().values()) {
            assertFalse(appender instanceof ConsoleAppender, "log lines would interrupt the game text");
        }
    }

    @Test
    void newlinesInLoggedTextCannotForgeExtraLines() throws IOException {
        String written = TestLog.writtenDuring(() -> LOG.info("typed text\nFORGED line"));
        assertFalse(written.contains("\nFORGED"), written);
    }
}
