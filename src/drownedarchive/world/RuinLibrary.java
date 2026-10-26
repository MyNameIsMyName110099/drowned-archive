package drownedarchive.world;

/*
 * All of the game's map content lives here, so the story text is in one place
 * and the rest of the code never has descriptions mixed into it.
 */
public final class RuinLibrary {

    private RuinLibrary() {
        // only static builders, no objects
    }

    /*
     * The first ruin. Row 0 is north. Empty cells are solid rock.
     *
     *   Collapsed Archive | Hall of Murals    | Sealed Vault
     *   (rock)            | Drowned Courtyard | Shrine of the Tide
     *   Kelp-Choked Stair | The Coral Gate *  | (rock)
     */
    public static Ruin coralGate() {
        Ruin ruin = new Ruin("The Coral Gate", 3, 3);

        Ruin.Chamber gate = ruin.addChamber(2, 1, "The Coral Gate",
                "A great arch of pink coral marks the entrance to the old city. Half of the arch "
                + "has gone grey and brittle, as if something drained the life out of it.");
        gate.addFeature("arch",
                "Up close, the grey coral is threaded with thin black veins. They pulse faintly "
                + "when you touch them.");
        gate.addFeature("statue",
                "A mermaid guard carved from stone holds a trident across the entrance. Someone "
                + "has scratched a warning into the base: TURN BACK.");

        Ruin.Chamber stair = ruin.addChamber(2, 0, "Kelp-Choked Stair",
                "A spiral stair winds down into darkness, but a wall of kelp has grown over it so "
                + "thick that you can't push through.");
        stair.addFeature("kelp",
                "The kelp is black at the roots and smells of rot. Whatever is wrong with the coral "
                + "is wrong with the kelp too.");

        Ruin.Chamber courtyard = ruin.addChamber(1, 1, "Drowned Courtyard",
                "An open courtyard where merfolk once gathered. A dry fountain stands in the "
                + "middle, and the sand is littered with broken shells.");
        courtyard.addFeature("fountain",
                "The fountain used to pump warm water from a vent below. The vent is choked with "
                + "black sludge.");
        courtyard.addFeature("shells",
                "These shells are cracked from the inside, as if whatever lived in them tried to "
                + "escape.", "shell");

        Ruin.Chamber shrine = ruin.addChamber(1, 2, "Shrine of the Tide",
                "A small shrine with a stone altar. Offerings of pearls lie scattered across the "
                + "floor, knocked over in a hurry.");
        shrine.addFeature("altar",
                "Carved into the altar is a prayer to the tide spirits, asking them to keep the "
                + "'sickness from the deep' away from the city.");

        Ruin.Chamber hall = ruin.addChamber(0, 1, "Hall of Murals",
                "A long hall lined with murals of the old mermaid kingdom. The paint is faded but "
                + "still readable in the light of your lantern.");
        hall.addFeature("mural",
                "The mural shows mermaid warriors kneeling before a figure wrapped in shadow. The "
                + "figure's face has been scraped away.");

        Ruin.Chamber archive = ruin.addChamber(0, 0, "Collapsed Archive",
                "Stone shelves lean against each other where the ceiling caved in. Tablets lie "
                + "everywhere, most of them shattered.");
        archive.addFeature("shelves",
                "The shelves were labelled by year. The newest shelf is empty, as if the record "
                + "keepers left in a hurry.", "shelf");
        archive.addFeature("tablets",
                "One unbroken tablet reads: 'The warriors went down to the vault to guard it. "
                + "They did not come back up the same.'", "tablet");

        Ruin.Chamber vault = ruin.addChamber(0, 2, "Sealed Vault",
                "A round stone door fills the far wall. Something heavy scrapes against it from "
                + "the other side.");
        vault.addFeature("door",
                "The door is sealed with a carved shell symbol. There's a slot in the middle, like "
                + "it's waiting for a key.");

        ruin.setStart(gate);
        return ruin;
    }
}
