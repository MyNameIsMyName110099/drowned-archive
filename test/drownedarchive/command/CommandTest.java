package drownedarchive.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CommandTest {

    @Test
    void verbThatNeedsANounCannotBeBuiltWithoutOne() {
        assertThrows(IllegalArgumentException.class, () -> new Command(Verb.SWIM, ""));
        assertThrows(IllegalArgumentException.class, () -> new Command(Verb.EXAMINE, null));
    }

    @Test
    void optionalNounDefaultsToEmpty() {
        assertEquals("", new Command(Verb.LOOK, null).getNoun());
    }

    @Test
    void equalCommandsHaveSameVerbAndNoun() {
        assertEquals(new Command(Verb.SWIM, "north"), new Command(Verb.SWIM, "north"));
        assertEquals(new Command(Verb.SWIM, "north").hashCode(), new Command(Verb.SWIM, "north").hashCode());
        assertNotEquals(new Command(Verb.SWIM, "north"), new Command(Verb.SWIM, "south"));
    }

    @Test
    void toStringShowsVerbAndNoun() {
        assertEquals("SWIM north", new Command(Verb.SWIM, "north").toString());
        assertEquals("LOOK", new Command(Verb.LOOK, "").toString());
    }
}
