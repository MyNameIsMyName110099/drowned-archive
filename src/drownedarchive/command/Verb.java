package drownedarchive.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/*
 * Every action the player can type. Each verb knows the words that mean it, whether
 * it needs a noun, and its line in the help list. What a verb actually DOES is up to
 * the GameEngine, which switches on the verb. That keeps this package from needing
 * to know anything about the game world.
 */
public enum Verb {
    SWIM(NounRule.REQUIRED, "Swim where?", "swim <direction>",
            "Swim north, south, east or west (or just type n, s, e, w).",
            "swim", "go", "move"),
    LOOK(NounRule.OPTIONAL, "", "look [thing]",
            "Describe the chamber you're in, or look at something in it.",
            "look", "l"),
    EXAMINE(NounRule.REQUIRED, "Examine what?", "examine <thing>",
            "Take a closer look at something in the chamber.",
            "examine", "x", "inspect"),
    HELP(NounRule.OPTIONAL, "", "help",
            "Show this list of commands.",
            "help", "?"),
    QUIT(NounRule.OPTIONAL, "", "quit",
            "Leave the game.",
            "quit", "exit");

    private final NounRule nounRule;
    private final String missingNounPrompt;
    private final String usage;
    private final String helpText;
    private final List<String> words;

    Verb(NounRule nounRule, String missingNounPrompt, String usage, String helpText, String... words) {
        // A verb that needs a noun must say what to ask for; any other verb must not.
        if ((nounRule == NounRule.REQUIRED) == missingNounPrompt.isEmpty()) {
            throw new IllegalStateException(name() + ": missing-noun prompt doesn't match its NounRule");
        }
        this.nounRule = nounRule;
        this.missingNounPrompt = missingNounPrompt;
        this.usage = usage;
        this.helpText = helpText;
        this.words = Collections.unmodifiableList(Arrays.asList(words));
    }

    public NounRule getNounRule() {
        return nounRule;
    }

    /* What the game asks when a REQUIRED noun is missing, like "Swim where?". */
    public String getMissingNounPrompt() {
        return missingNounPrompt;
    }

    public String getUsage() {
        return usage;
    }

    public String getHelpText() {
        return helpText;
    }

    public static Optional<Verb> fromWord(String word) {
        if (word == null) {
            return Optional.empty();
        }
        String cleaned = word.trim().toLowerCase(Locale.ROOT);
        for (Verb verb : values()) {
            if (verb.words.contains(cleaned)) {
                return Optional.of(verb);
            }
        }
        return Optional.empty();
    }
}
