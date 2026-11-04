package drownedarchive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import drownedarchive.world.Ruin;

/* Tests how Main reports failures and that it always cleans up after the game loop. */
class MainTest {

    /* A reader whose read and close can be told to fail, and which remembers being closed. */
    private static class FakeReader extends Reader {
        private final Reader typed;
        private final Exception readFailure;
        private final IOException closeFailure;
        private boolean closed = false;

        FakeReader(String typed, Exception readFailure, IOException closeFailure) {
            this.typed = new StringReader(typed);
            this.readFailure = readFailure;
            this.closeFailure = closeFailure;
        }

        @Override
        public int read(char[] buffer, int offset, int length) throws IOException {
            if (readFailure instanceof IOException) {
                throw (IOException) readFailure;
            }
            if (readFailure instanceof RuntimeException) {
                throw (RuntimeException) readFailure;
            }
            return typed.read(buffer, offset, length);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if (closeFailure != null) {
                throw closeFailure;
            }
        }
    }

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private int play(Reader reader) {
        Ruin ruin = new Ruin("Test Ruin", 1, 1);
        ruin.setStart(ruin.addChamber(0, 0, "Gate", "A broken gate."));
        return Main.play(new GameEngine(ruin), new BufferedReader(reader),
                new PrintStream(out, true), new PrintStream(err, true));
    }

    @Test
    void playReturnsSuccessWhenPlayerQuits() {
        assertEquals(Main.EXIT_SUCCESS, play(new StringReader("quit\n")));
        assertEquals("", err.toString());
    }

    @Test
    void playReturnsSuccessWhenInputEndsImmediately() {
        assertEquals(Main.EXIT_SUCCESS, play(new StringReader("")));
        assertEquals("", err.toString());
    }

    @Test
    void playReportsFriendlyMessageAndInputErrorCodeWhenReadFails() {
        int result = play(new FakeReader("", new IOException("disk on fire"), null));
        assertEquals(Main.EXIT_INPUT_ERROR, result);
        assertTrue(err.toString().contains("couldn't read your input"), err.toString());
        assertTrue(err.toString().contains("disk on fire"), err.toString());
    }

    @Test
    void playReportsFriendlyMessageAndUnexpectedErrorCodeWhenGameThrows() {
        int result = play(new FakeReader("", new IllegalStateException("broken rule"), null));
        assertEquals(Main.EXIT_UNEXPECTED_ERROR, result);
        assertTrue(err.toString().contains("Something went wrong"), err.toString());
        assertFalse(err.toString().contains("IllegalStateException"), "players shouldn't see a stack trace");
    }

    @Test
    void playClosesInputWhenPlayerQuits() {
        FakeReader reader = new FakeReader("quit\n", null, null);
        play(reader);
        assertTrue(reader.closed);
    }

    @Test
    void playClosesInputWhenReadFails() {
        FakeReader reader = new FakeReader("", new IOException("disk on fire"), null);
        play(reader);
        assertTrue(reader.closed);
    }

    @Test
    void playKeepsResultWhenClosingInputFails() {
        int result = play(new FakeReader("quit\n", null, new IOException("won't close")));
        assertEquals(Main.EXIT_SUCCESS, result);
        assertTrue(err.toString().contains("won't close"), err.toString());
    }
}
