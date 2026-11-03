package drownedarchive.character;

import java.util.Objects;

import drownedarchive.world.Ruin;

/*
 * The mermaid archaeologist. For now the player only needs to know where they are;
 * health, inventory and the journal come in later milestones.
 */
public class Player {
    private Ruin.Chamber location;

    public Player(Ruin.Chamber start) {
        this.location = Objects.requireNonNull(start, "start");
    }

    public Ruin.Chamber getLocation() {
        return location;
    }

    public void moveTo(Ruin.Chamber chamber) {
        this.location = Objects.requireNonNull(chamber, "chamber");
    }
}
