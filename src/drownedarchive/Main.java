package drownedarchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import drownedarchive.world.RuinLibrary;

/* Starts the game in the console. */
public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    static final int EXIT_SUCCESS = 0;
    static final int EXIT_INPUT_ERROR = 1;
    static final int EXIT_UNEXPECTED_ERROR = 2;

    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        // Exiting only after play() returns: System.exit inside a catch would skip its finally.
        System.exit(play(() -> new GameEngine(RuinLibrary.coralGate()), in, System.out, System.err));
    }

    /*
     * The top level of the game's exception handling. Anything the game can't recover from
     * ends up here, so the player gets a readable message instead of a stack trace.
     * The game is built inside the try because a mistake in a ruin's data throws while
     * it's being built. Returns the exit code for the process.
     */
    static int play(Supplier<GameEngine> newGame, BufferedReader in, PrintStream out, PrintStream err) {
        LOG.info("Game session started");
        try {
            newGame.get().run(in, out);
            return EXIT_SUCCESS;
        } catch (IOException e) {
            LOG.error("Could not read player input", e);
            err.println("The game couldn't read your input and has to stop: " + e.getMessage());
            return EXIT_INPUT_ERROR;
        } catch (RuntimeException e) {
            // The player only sees a short message, so the log is the one record of what broke.
            LOG.error("Unexpected failure in the game", e);
            err.println("Something went wrong in the game and it has to stop.");
            return EXIT_UNEXPECTED_ERROR;
        } finally {
            closeInput(in, err);
            LOG.info("Game session ended");
        }
    }

    // A failed close is reported but can't be allowed to replace the result of the game.
    private static void closeInput(BufferedReader in, PrintStream err) {
        try {
            in.close();
        } catch (IOException e) {
            LOG.warn("Could not close player input", e);
            err.println("The game couldn't close its input: " + e.getMessage());
        }
    }
}
