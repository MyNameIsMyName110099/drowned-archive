package drownedarchive.character;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import drownedarchive.world.Ruin;

class PlayerTest {

    private final Ruin ruin = new Ruin("Test Ruin", 1, 2);
    private final Ruin.Chamber left = ruin.addChamber(0, 0, "Left", "Left room.");
    private final Ruin.Chamber right = ruin.addChamber(0, 1, "Right", "Right room.");

    @Test
    void startsWhereItIsPlaced() {
        assertSame(left, new Player(left).getLocation());
    }

    @Test
    void moveToChangesLocation() {
        Player player = new Player(left);
        player.moveTo(right);
        assertSame(right, player.getLocation());
    }

    @Test
    void cannotBeNowhere() {
        assertThrows(NullPointerException.class, () -> new Player(null));
        assertThrows(NullPointerException.class, () -> new Player(left).moveTo(null));
    }
}
