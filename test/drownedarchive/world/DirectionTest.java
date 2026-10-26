package drownedarchive.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class DirectionTest {

    @Test
    void fromWordAcceptsFullNames() {
        assertEquals(Optional.of(Direction.NORTH), Direction.fromWord("north"));
        assertEquals(Optional.of(Direction.SOUTH), Direction.fromWord("south"));
        assertEquals(Optional.of(Direction.EAST), Direction.fromWord("east"));
        assertEquals(Optional.of(Direction.WEST), Direction.fromWord("west"));
    }

    @Test
    void fromWordAcceptsSingleLetters() {
        assertEquals(Optional.of(Direction.NORTH), Direction.fromWord("n"));
        assertEquals(Optional.of(Direction.SOUTH), Direction.fromWord("s"));
        assertEquals(Optional.of(Direction.EAST), Direction.fromWord("e"));
        assertEquals(Optional.of(Direction.WEST), Direction.fromWord("w"));
    }

    @Test
    void fromWordIgnoresCaseAndSpaces() {
        assertEquals(Optional.of(Direction.NORTH), Direction.fromWord("  NoRtH "));
        assertEquals(Optional.of(Direction.WEST), Direction.fromWord("W"));
    }

    @Test
    void fromWordRejectsUnknownWord() {
        assertFalse(Direction.fromWord("up").isPresent());
        assertFalse(Direction.fromWord("northeast").isPresent());
    }

    @Test
    void fromWordRejectsNullAndBlank() {
        assertFalse(Direction.fromWord(null).isPresent());
        assertFalse(Direction.fromWord("   ").isPresent());
    }

    @Test
    void toStringIsTheLowercaseWord() {
        assertEquals("north", Direction.NORTH.toString());
    }

    @Test
    void oppositeOfEachDirection() {
        assertEquals(Direction.SOUTH, Direction.NORTH.opposite());
        assertEquals(Direction.NORTH, Direction.SOUTH.opposite());
        assertEquals(Direction.WEST, Direction.EAST.opposite());
        assertEquals(Direction.EAST, Direction.WEST.opposite());
    }
}
