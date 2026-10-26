package drownedarchive.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RuinTest {

    // Layout used by most tests (row 0 is north):
    //   [hall ] [ -- ]
    //   [gate ] [yard]
    private Ruin ruin;
    private Ruin.Chamber hall;
    private Ruin.Chamber gate;
    private Ruin.Chamber yard;

    @BeforeEach
    void buildRuin() {
        ruin = new Ruin("Test Ruin", 2, 2);
        hall = ruin.addChamber(0, 0, "Hall", "A long hall.");
        gate = ruin.addChamber(1, 0, "Gate", "A broken gate.");
        yard = ruin.addChamber(1, 1, "Yard", "An open yard.");
    }

    @Test
    void neighbourReturnsAdjacentChamber() {
        assertEquals(Optional.of(hall), gate.neighbour(Direction.NORTH));
        assertEquals(Optional.of(yard), gate.neighbour(Direction.EAST));
        assertEquals(Optional.of(gate), yard.neighbour(Direction.WEST));
    }

    @Test
    void neighbourIsEmptyAtGridEdge() {
        assertFalse(gate.neighbour(Direction.SOUTH).isPresent());
        assertFalse(gate.neighbour(Direction.WEST).isPresent());
        assertFalse(hall.neighbour(Direction.NORTH).isPresent());
    }

    @Test
    void neighbourIsEmptyForSolidRock() {
        assertFalse(hall.neighbour(Direction.EAST).isPresent());
        assertFalse(yard.neighbour(Direction.NORTH).isPresent());
    }

    @Test
    void exitsListsOnlyOpenDirectionsInOrder() {
        assertEquals(Arrays.asList(Direction.NORTH, Direction.EAST), gate.exits());
        assertEquals(Arrays.asList(Direction.SOUTH), hall.exits());
    }

    @Test
    void exitsCannotBeChangedByCaller() {
        assertThrows(UnsupportedOperationException.class, () -> gate.exits().clear());
    }

    @Test
    void addChamberOutsideGridThrows() {
        assertThrows(IllegalArgumentException.class, () -> ruin.addChamber(2, 0, "X", "x"));
        assertThrows(IllegalArgumentException.class, () -> ruin.addChamber(0, -1, "X", "x"));
    }

    @Test
    void addChamberTwiceInSameCellThrows() {
        assertThrows(IllegalStateException.class, () -> ruin.addChamber(0, 0, "Again", "again"));
    }

    @Test
    void addChamberWithMissingTextThrows() {
        assertThrows(IllegalArgumentException.class, () -> ruin.addChamber(0, 1, null, "text"));
        assertThrows(IllegalArgumentException.class, () -> ruin.addChamber(0, 1, "Title", "  "));
    }

    @Test
    void featureLookupIgnoresCaseAndSpaces() {
        gate.addFeature("Statue", "A statue of a mermaid queen.");
        assertEquals(Optional.of("A statue of a mermaid queen."), gate.feature("  sTaTuE "));
    }

    @Test
    void featureCanBeFoundByAlias() {
        gate.addFeature("tablets", "Old tablets.", "tablet");
        assertEquals(Optional.of("Old tablets."), gate.feature("tablet"));
        assertEquals(Optional.of("Old tablets."), gate.feature("tablets"));
    }

    @Test
    void unknownFeatureIsEmpty() {
        assertFalse(gate.feature("statue").isPresent());
        assertFalse(gate.feature(null).isPresent());
    }

    @Test
    void featureNamesListMainNamesInOrderWithoutAliases() {
        gate.addFeature("statue", "s");
        gate.addFeature("tablets", "t", "tablet");
        assertEquals(Arrays.asList("statue", "tablets"), gate.featureNames());
    }

    @Test
    void addFeatureWithMissingNameOrTextThrows() {
        assertThrows(IllegalArgumentException.class, () -> gate.addFeature(null, "text"));
        assertThrows(IllegalArgumentException.class, () -> gate.addFeature(" ", "text"));
        assertThrows(IllegalArgumentException.class, () -> gate.addFeature("door", null));
    }

    @Test
    void duplicateFeatureNameThrows() {
        gate.addFeature("mural", "first");
        assertThrows(IllegalStateException.class, () -> gate.addFeature("Mural ", "second"));
        assertThrows(IllegalStateException.class, () -> gate.addFeature("painting", "third", "mural"));
    }

    @Test
    void allChambersListsEveryChamberRowByRow() {
        assertEquals(Arrays.asList(hall, gate, yard), ruin.allChambers());
    }

    @Test
    void startChamberIsTheOneMarkedStart() {
        ruin.setStart(gate);
        assertSame(gate, ruin.getStart());
    }

    @Test
    void startCannotBeAChamberFromAnotherRuin() {
        Ruin other = new Ruin("Other", 1, 1);
        Ruin.Chamber stranger = other.addChamber(0, 0, "Elsewhere", "Somewhere else.");
        assertThrows(IllegalArgumentException.class, () -> ruin.setStart(stranger));
    }

    @Test
    void startCanOnlyBeSetOnce() {
        ruin.setStart(gate);
        assertThrows(IllegalStateException.class, () -> ruin.setStart(hall));
    }

    @Test
    void startBeforeItIsSetThrows() {
        assertThrows(IllegalStateException.class, ruin::getStart);
    }

    @Test
    void rejectsZeroOrNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new Ruin("Bad", 0, 3));
        assertThrows(IllegalArgumentException.class, () -> new Ruin("Bad", 3, -1));
    }

    @Test
    void rejectsMissingName() {
        assertThrows(IllegalArgumentException.class, () -> new Ruin(" ", 1, 1));
    }
}
