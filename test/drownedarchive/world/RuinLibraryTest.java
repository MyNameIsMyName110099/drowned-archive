package drownedarchive.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/*
 * These check the actual game content, so a typo in a row/column number
 * (like a chamber that can't be reached) gets caught here instead of mid-game.
 */
class RuinLibraryTest {

    private final Ruin coralGate = RuinLibrary.coralGate();

    @Test
    void coralGateStartsAtTheGate() {
        assertEquals("The Coral Gate", coralGate.getStart().getTitle());
    }

    @Test
    void everyChamberReachableFromStart() {
        Set<Ruin.Chamber> visited = new HashSet<>();
        Deque<Ruin.Chamber> toVisit = new ArrayDeque<>();
        toVisit.add(coralGate.getStart());
        while (!toVisit.isEmpty()) {
            Ruin.Chamber current = toVisit.poll();
            if (visited.add(current)) {
                for (Direction direction : current.exits()) {
                    current.neighbour(direction).ifPresent(toVisit::add);
                }
            }
        }
        assertEquals(new HashSet<>(coralGate.allChambers()), visited);
    }

    @Test
    void everyChamberHasTitleDescriptionAndAnExit() {
        for (Ruin.Chamber chamber : coralGate.allChambers()) {
            assertFalse(chamber.getTitle().trim().isEmpty(), chamber + " has no title");
            assertFalse(chamber.getDescription().trim().isEmpty(), chamber + " has no description");
            assertFalse(chamber.exits().isEmpty(), chamber + " has no exits");
        }
    }

    @Test
    void everyChamberHasSomethingToExamine() {
        for (Ruin.Chamber chamber : coralGate.allChambers()) {
            assertFalse(chamber.featureNames().isEmpty(), chamber + " has nothing to examine");
        }
    }
}
