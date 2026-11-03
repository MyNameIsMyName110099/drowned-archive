package drownedarchive.command;

import java.util.Objects;

/*
 * One parsed player command: a verb plus an optional noun ("swim" + "north").
 * Immutable, so once the parser hands it over nothing can change it.
 */
public final class Command {
    private final Verb verb;
    private final String noun;

    public Command(Verb verb, String noun) {
        this.verb = Objects.requireNonNull(verb, "verb");
        this.noun = noun == null ? "" : noun;
        if (verb.getNounRule() == NounRule.REQUIRED && this.noun.isEmpty()) {
            throw new IllegalArgumentException(verb + " needs a noun");
        }
    }

    public Verb getVerb() {
        return verb;
    }

    /* Empty string when the player didn't type one. */
    public String getNoun() {
        return noun;
    }

    public boolean hasNoun() {
        return !noun.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Command)) {
            return false;
        }
        Command that = (Command) other;
        return verb == that.verb && noun.equals(that.noun);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verb, noun);
    }

    @Override
    public String toString() {
        return hasNoun() ? verb + " " + noun : verb.toString();
    }
}
