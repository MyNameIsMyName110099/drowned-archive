package drownedarchive.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    @Test
    void parsesVerbAndNoun() throws InvalidCommandException {
        assertEquals(new Command(Verb.SWIM, "north"), parser.parse("swim north"));
    }

    @Test
    void aliasesMapToSameVerb() throws InvalidCommandException {
        assertEquals(Verb.SWIM, parser.parse("go north").getVerb());
        assertEquals(Verb.SWIM, parser.parse("move north").getVerb());
        assertEquals(Verb.EXAMINE, parser.parse("x mural").getVerb());
        assertEquals(Verb.EXAMINE, parser.parse("inspect mural").getVerb());
        assertEquals(Verb.LOOK, parser.parse("l").getVerb());
        assertEquals(Verb.HELP, parser.parse("?").getVerb());
        assertEquals(Verb.QUIT, parser.parse("exit").getVerb());
    }

    @Test
    void ignoresCaseAndExtraSpaces() throws InvalidCommandException {
        assertEquals(new Command(Verb.SWIM, "north"), parser.parse("   SWIM    North  "));
    }

    @Test
    void bareDirectionMeansSwim() throws InvalidCommandException {
        assertEquals(new Command(Verb.SWIM, "n"), parser.parse("n"));
        assertEquals(new Command(Verb.SWIM, "north"), parser.parse("North"));
    }

    @Test
    void multiWordNounKept() throws InvalidCommandException {
        assertEquals(new Command(Verb.EXAMINE, "broken statue"), parser.parse("examine broken statue"));
    }

    @Test
    void skipsLeadingFillerWords() throws InvalidCommandException {
        assertEquals(new Command(Verb.LOOK, "mural"), parser.parse("look at the mural"));
        assertEquals(new Command(Verb.SWIM, "north"), parser.parse("swim to the north"));
    }

    @Test
    void keepsFillerWordsInsideTheNoun() throws InvalidCommandException {
        assertEquals(new Command(Verb.EXAMINE, "statue of the queen"), parser.parse("examine the statue of the queen"));
    }

    @Test
    void ignoresPunctuationAtTheEndOfWords() throws InvalidCommandException {
        assertEquals(new Command(Verb.LOOK, "mural"), parser.parse("look at the mural."));
        assertEquals(new Command(Verb.SWIM, "north"), parser.parse("north!"));
        assertEquals(new Command(Verb.SWIM, "north"), parser.parse("swim, north"));
    }

    @Test
    void questionMarkOnItsOwnStillMeansHelp() throws InvalidCommandException {
        assertEquals(Verb.HELP, parser.parse("?").getVerb());
    }

    @Test
    void optionalNounMayBeMissing() throws InvalidCommandException {
        Command look = parser.parse("look");
        assertEquals(Verb.LOOK, look.getVerb());
        assertFalse(look.hasNoun());
        assertEquals("", look.getNoun());
    }

    @Test
    void blankInputThrowsWithHint() {
        InvalidCommandException e = assertThrows(InvalidCommandException.class, () -> parser.parse("   "));
        assertTrue(e.getMessage().contains("help"));
    }

    @Test
    void nullInputThrows() {
        assertThrows(InvalidCommandException.class, () -> parser.parse(null));
    }

    @Test
    void unknownVerbThrowsNamingTheWord() {
        InvalidCommandException e = assertThrows(InvalidCommandException.class, () -> parser.parse("dance wildly"));
        assertTrue(e.getMessage().contains("dance"));
    }

    @Test
    void requiredNounMissingThrowsVerbPrompt() {
        InvalidCommandException swim = assertThrows(InvalidCommandException.class, () -> parser.parse("swim"));
        assertEquals("Swim where?", swim.getMessage());
        InvalidCommandException examine = assertThrows(InvalidCommandException.class, () -> parser.parse("examine the"));
        assertEquals("Examine what?", examine.getMessage());
    }
}
