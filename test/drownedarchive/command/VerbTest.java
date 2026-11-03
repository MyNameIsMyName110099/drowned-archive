package drownedarchive.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class VerbTest {

    @Test
    void fromWordFindsEveryWordOfAVerb() {
        assertEquals(Optional.of(Verb.SWIM), Verb.fromWord("swim"));
        assertEquals(Optional.of(Verb.SWIM), Verb.fromWord("go"));
        assertEquals(Optional.of(Verb.EXAMINE), Verb.fromWord("x"));
    }

    @Test
    void fromWordIgnoresCase() {
        assertEquals(Optional.of(Verb.QUIT), Verb.fromWord("QUIT"));
    }

    @Test
    void fromWordRejectsUnknownAndNull() {
        assertFalse(Verb.fromWord("dance").isPresent());
        assertFalse(Verb.fromWord(null).isPresent());
    }

    @Test
    void onlyVerbsThatNeedANounHaveAMissingNounPrompt() {
        for (Verb verb : Verb.values()) {
            boolean required = verb.getNounRule() == NounRule.REQUIRED;
            assertEquals(required, !verb.getMissingNounPrompt().isEmpty(), verb.toString());
        }
    }

    @Test
    void everyVerbHasUsageAndHelpText() {
        for (Verb verb : Verb.values()) {
            assertFalse(verb.getUsage().isEmpty(), verb + " has no usage");
            assertFalse(verb.getHelpText().isEmpty(), verb + " has no help text");
        }
    }
}
