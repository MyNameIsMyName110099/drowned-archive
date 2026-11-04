package drownedarchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import drownedarchive.world.RuinLibrary;

/* Starts the game in the console. */
public class Main {
    static final int EXIT_SUCCESS = 0;
    static final int EXIT_INPUT_ERROR = 1;
    static final int EXIT_UNEXPECTED_ERROR = 2;

    public static void main(String[] args) {
        GameEngine game = new GameEngine(RuinLibrary.coralGate());
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        // Exiting only after play() returns: System.exit inside a catch would skip its finally.
        System.exit(play(game, in, System.out, System.err));
    }

    /*
     * The top level of the game's exception handling. Anything the game can't recover from
     * ends up here, so the player gets a readable message instead of a stack trace.
     * Returns the exit code for the process.
     */
    static int play(GameEngine game, BufferedReader in, PrintStream out, PrintStream err) {
        try {
            game.run(in, out);
            return EXIT_SUCCESS;
        } catch (IOException e) {
            err.println("The game couldn't read your input and has to stop: " + e.getMessage());
            return EXIT_INPUT_ERROR;
        } catch (RuntimeException e) {
            err.println("Something went wrong in the game and it has to stop.");
            return EXIT_UNEXPECTED_ERROR;
        } finally {
            closeInput(in, err);
        }
    }

    // A failed close is reported but can't be allowed to replace the result of the game.
    private static void closeInput(BufferedReader in, PrintStream err) {
        try {
            in.close();
        } catch (IOException e) {
            err.println("The game couldn't close its input: " + e.getMessage());
        }
    }
}
