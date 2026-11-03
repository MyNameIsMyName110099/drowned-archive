package drownedarchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import drownedarchive.character.Player;
import drownedarchive.command.Command;
import drownedarchive.command.CommandParser;
import drownedarchive.command.InvalidCommandException;
import drownedarchive.command.Verb;
import drownedarchive.world.Direction;
import drownedarchive.world.Ruin;

/*
 * Runs the game. handle() takes one line the player typed and returns what the game
 * says back. It never prints anything itself, which means every rule can be tested
 * without a console.
 */
public class GameEngine {
    private static final String PROMPT = "> ";
    private static final String GOODBYE = "You swim back toward the surface. The answers will have to wait.";
    static final int CONSOLE_WIDTH = 80;

    private final CommandParser parser = new CommandParser();
    private final Ruin ruin;
    private final Player player;
    private boolean running = true;

    public GameEngine(Ruin ruin) {
        this.ruin = ruin;
        this.player = new Player(ruin.getStart());
    }

    public String intro() {
        return "THE DROWNED ARCHIVE\n\n"
                + "Your people are dying, and no one can say why. As the city's archaeologist, you "
                + "have come to the ruins of " + ruin.getName() + " to search for answers.\n"
                + "(Type 'help' at any time to see what you can do.)\n\n"
                + describe(player.getLocation());
    }

    public boolean isRunning() {
        return running;
    }

    /*
     * The main game loop: read a line, handle it, print the reply, repeat until the
     * player quits or the input runs out (Ctrl+Z / Ctrl+D). Read errors are passed up
     * to Main instead of being hidden here.
     */
    public void run(BufferedReader in, PrintStream out) throws IOException {
        out.println(TextWrap.wrap(intro(), CONSOLE_WIDTH));
        while (running) {
            out.println();
            out.print(PROMPT);
            out.flush();
            String line = in.readLine();
            if (line == null) {
                out.println();
                out.println(GOODBYE);
                return;
            }
            out.println(TextWrap.wrap(handle(line), CONSOLE_WIDTH));
        }
    }

    public String handle(String line) {
        if (!running) {
            throw new IllegalStateException("The game is over; no more commands can be handled");
        }
        Command command;
        try {
            command = parser.parse(line);
        } catch (InvalidCommandException e) {
            return e.getMessage();
        }
        switch (command.getVerb()) {
            case SWIM:    return swim(command.getNoun());
            case LOOK:    return command.hasNoun() ? examine(command.getNoun()) : describe(player.getLocation());
            case EXAMINE: return examine(command.getNoun());
            case HELP:    return help();
            case QUIT:    return quit();
            // Only reachable if a new Verb is added without handling it here.
            default:      throw new IllegalStateException("No handler for " + command.getVerb());
        }
    }

    private String swim(String directionWord) {
        Optional<Direction> direction = Direction.fromWord(directionWord);
        if (!direction.isPresent()) {
            return "You can't swim \"" + directionWord + "\". Try north, south, east or west.";
        }
        Optional<Ruin.Chamber> next = player.getLocation().neighbour(direction.get());
        if (!next.isPresent()) {
            return "Solid rock blocks the way " + direction.get() + ".";
        }
        player.moveTo(next.get());
        return describe(next.get());
    }

    private String examine(String thing) {
        return player.getLocation().feature(thing)
                .orElse("You don't see any \"" + thing + "\" here.");
    }

    private String help() {
        // Pad every usage to the longest one so the descriptions line up in a column.
        int usageWidth = 0;
        for (Verb verb : Verb.values()) {
            usageWidth = Math.max(usageWidth, verb.getUsage().length());
        }
        StringBuilder text = new StringBuilder("Commands:");
        for (Verb verb : Verb.values()) {
            text.append('\n').append(String.format("  %-" + usageWidth + "s   %s", verb.getUsage(), verb.getHelpText()));
        }
        return text.toString();
    }

    private String quit() {
        running = false;
        return GOODBYE;
    }

    private String describe(Ruin.Chamber chamber) {
        StringBuilder text = new StringBuilder();
        text.append("== ").append(chamber.getTitle()).append(" ==\n");
        text.append(chamber.getDescription()).append('\n');
        if (!chamber.featureNames().isEmpty()) {
            text.append("You notice: ").append(joinWords(chamber.featureNames())).append(".\n");
        }
        text.append("Exits: ").append(joinWords(chamber.exits())).append('.');
        return text.toString();
    }

    private static String joinWords(List<?> things) {
        if (things.isEmpty()) {
            return "none";
        }
        return things.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }
}
