package drownedarchive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import drownedarchive.world.Ruin;

/* Tests the console loop by feeding it typed lines and capturing what it prints. */
class GameEngineRunTest {

    private GameEngine newGame() {
        Ruin ruin = new Ruin("Test Ruin", 1, 2);
        ruin.setStart(ruin.addChamber(0, 0, "Gate", "A broken gate."));
        ruin.addChamber(0, 1, "Yard", "An open yard.");
        return new GameEngine(ruin);
    }

    private String play(GameEngine game, String typed) throws IOException {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        game.run(new BufferedReader(new StringReader(typed)), new PrintStream(captured, true));
        return captured.toString();
    }

    private static int count(String text, String part) {
        return text.split(Pattern.quote(part), -1).length - 1;
    }

    @Test
    void runPrintsIntroPromptAndResponses() throws IOException {
        String output = play(newGame(), "swim east\nquit\n");
        assertTrue(output.contains("THE DROWNED ARCHIVE"));
        assertTrue(output.contains("> "));
        assertTrue(output.contains("== Yard =="));
    }

    @Test
    void runStopsOnQuit() throws IOException {
        GameEngine game = newGame();
        String output = play(game, "quit\nswim east\n");
        assertTrue(output.contains("surface"));
        assertEquals(0, count(output, "== Yard =="));
    }

    @Test
    void runStopsAtEndOfInput() throws IOException {
        String output = play(newGame(), "look");
        assertEquals(2, count(output, "== Gate =="));
    }

    @Test
    void endOfInputSaysGoodbyeOnItsOwnLine() throws IOException {
        String output = play(newGame(), "");
        assertTrue(output.trim().endsWith("surface. The answers will have to wait."));
        assertTrue(output.contains("> " + System.lineSeparator()));
    }

    @Test
    void runWrapsLongTextToConsoleWidth() throws IOException {
        Ruin ruin = new Ruin("Wide Ruin", 1, 1);
        StringBuilder longDescription = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            longDescription.append("word ");
        }
        ruin.setStart(ruin.addChamber(0, 0, "Wide", longDescription.toString()));
        String output = play(new GameEngine(ruin), "quit\n");
        for (String line : output.split("\\R")) {
            assertTrue(line.length() <= GameEngine.CONSOLE_WIDTH, "too long: " + line);
        }
    }

    @Test
    void runPassesReadErrorsUp() {
        Reader broken = new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                throw new IOException("keyboard unplugged");
            }

            @Override
            public void close() {
            }
        };
        assertThrows(IOException.class,
                () -> newGame().run(new BufferedReader(broken), new PrintStream(new ByteArrayOutputStream())));
    }
}
