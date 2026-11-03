package drownedarchive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import drownedarchive.command.Verb;
import drownedarchive.world.Ruin;

class GameEngineTest {

    // Small ruin just for these tests (row 0 is north):
    //   [Hall] [rock]
    //   [Gate] [Yard]
    private GameEngine game;

    @BeforeEach
    void startGame() {
        Ruin ruin = new Ruin("Test Ruin", 2, 2);
        ruin.addChamber(0, 0, "Hall", "A long hall.").addFeature("mural", "A faded mural.");
        Ruin.Chamber gate = ruin.addChamber(1, 0, "Gate", "A broken gate.");
        gate.addFeature("statue", "A stone guard.");
        gate.addFeature("arch", "A coral arch.");
        ruin.addChamber(1, 1, "Yard", "An open yard.");
        ruin.setStart(gate);
        game = new GameEngine(ruin);
    }

    @Test
    void introDescribesStartChamber() {
        String intro = game.intro();
        assertTrue(intro.contains("Gate"));
        assertTrue(intro.contains("A broken gate."));
        assertTrue(intro.contains("help"));
    }

    @Test
    void swimMovesAndDescribesNewChamber() {
        String reply = game.handle("swim north");
        assertTrue(reply.contains("Hall"));
        assertTrue(reply.contains("A long hall."));
        assertEquals("A faded mural.", game.handle("examine mural"));
    }

    @Test
    void bareDirectionShortcutMoves() {
        assertTrue(game.handle("e").contains("Yard"));
    }

    @Test
    void swimIntoWallStaysPutWithMessage() {
        String reply = game.handle("swim south");
        assertTrue(reply.contains("south"));
        assertTrue(game.handle("look").contains("Gate"));
    }

    @Test
    void swimIntoSolidRockStaysPut() {
        game.handle("swim north");
        game.handle("swim east");
        assertTrue(game.handle("look").contains("Hall"));
    }

    @Test
    void swimWithNonDirectionExplainsValidDirections() {
        String reply = game.handle("swim up");
        assertTrue(reply.contains("up"));
        assertTrue(reply.contains("north, south, east or west"));
    }

    @Test
    void lookListsTitleFeaturesAndExits() {
        String reply = game.handle("look");
        assertTrue(reply.contains("Gate"));
        assertTrue(reply.contains("statue, arch"));
        assertTrue(reply.contains("north, east"));
    }

    @Test
    void lookSkipsNoticeLineWhenThereIsNothingToExamine() {
        String reply = game.handle("swim east");
        assertTrue(reply.contains("Yard"));
        assertFalse(reply.contains("You notice"));
    }

    @Test
    void chamberWithNoExitsSaysSo() {
        Ruin tiny = new Ruin("Tiny", 1, 1);
        tiny.setStart(tiny.addChamber(0, 0, "Cell", "A sealed cell."));
        assertTrue(new GameEngine(tiny).handle("look").contains("Exits: none."));
    }

    @Test
    void lookWithNounActsLikeExamine() {
        assertEquals("A stone guard.", game.handle("look at the statue"));
    }

    @Test
    void examineKnownFeatureShowsText() {
        assertEquals("A coral arch.", game.handle("examine ARCH"));
    }

    @Test
    void examineUnknownFeatureSaysNotHere() {
        String reply = game.handle("examine mural");
        assertTrue(reply.contains("mural"));
        assertTrue(reply.contains("don't see"));
    }

    @Test
    void helpListsEveryVerb() {
        String reply = game.handle("help");
        for (Verb verb : Verb.values()) {
            assertTrue(reply.contains(verb.getUsage()), "help is missing " + verb);
        }
    }

    @Test
    void helpUsesPlainLineBreaksAndLinesUpDescriptions() {
        String reply = game.handle("help");
        assertFalse(reply.contains("\r"));
        String[] lines = reply.split("\n");
        int column = -1;
        for (Verb verb : Verb.values()) {
            int start = -1;
            for (String line : lines) {
                if (line.contains(verb.getHelpText())) {
                    start = line.indexOf(verb.getHelpText());
                }
            }
            if (column == -1) {
                column = start;
            }
            assertEquals(column, start, "help text for " + verb + " is out of line");
        }
    }

    @Test
    void quitStopsTheGame() {
        assertTrue(game.isRunning());
        game.handle("quit");
        assertFalse(game.isRunning());
    }

    @Test
    void handleAfterQuitThrows() {
        game.handle("quit");
        assertThrows(IllegalStateException.class, () -> game.handle("look"));
    }

    @Test
    void invalidInputReturnsErrorAndKeepsRunning() {
        String reply = game.handle("dance");
        assertTrue(reply.contains("dance"));
        assertTrue(game.isRunning());
        assertEquals("Swim where?", game.handle("swim"));
    }
}
