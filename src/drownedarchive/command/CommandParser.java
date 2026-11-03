package drownedarchive.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import drownedarchive.world.Direction;

/*
 * The verb-noun parser. Turns a line like "look at the mural" into
 * Command(LOOK, "mural"). It only checks that the command makes sense as a sentence -
 * whether there actually IS a mural here is the GameEngine's job.
 */
public class CommandParser {

    // Little words people type that don't change the meaning ("look AT THE mural").
    private static final Set<String> FILLER_WORDS =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList("at", "the", "a", "an", "to", "into")));

    public Command parse(String input) throws InvalidCommandException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidCommandException("Type a command, or 'help' to see what you can do.");
        }
        List<String> words = new ArrayList<>();
        for (String word : input.trim().toLowerCase(Locale.ROOT).split("\\s+")) {
            words.add(withoutTrailingPunctuation(word));
        }
        String first = words.get(0);

        // Typing just "n" or "north" is a shortcut for swimming that way.
        if (words.size() == 1 && Direction.fromWord(first).isPresent()) {
            return new Command(Verb.SWIM, first);
        }

        Verb verb = Verb.fromWord(first).orElseThrow(() -> new InvalidCommandException(
                "I don't know how to \"" + first + "\". Type 'help' to see what you can do."));

        String noun = nounFrom(words.subList(1, words.size()));
        if (verb.getNounRule() == NounRule.REQUIRED && noun.isEmpty()) {
            throw new InvalidCommandException(verb.getMissingNounPrompt());
        }
        return new Command(verb, noun);
    }

    // "mural." and "north!" should still work. A word that is ONLY punctuation, like
    // "?" (the help shortcut), is left alone.
    private String withoutTrailingPunctuation(String word) {
        String stripped = word.replaceAll("[.,!?;:]+$", "");
        return stripped.isEmpty() ? word : stripped;
    }

    // Skips filler words only at the start, so "statue of the queen" stays whole.
    private String nounFrom(List<String> rest) {
        int start = 0;
        while (start < rest.size() && FILLER_WORDS.contains(rest.get(start))) {
            start++;
        }
        return String.join(" ", rest.subList(start, rest.size()));
    }
}
