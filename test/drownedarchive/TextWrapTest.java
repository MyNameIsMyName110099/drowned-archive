package drownedarchive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TextWrapTest {

    @Test
    void shortTextIsUnchanged() {
        assertEquals("A short line.", TextWrap.wrap("A short line.", 20));
    }

    @Test
    void longLineBreaksBetweenWords() {
        assertEquals("one two\nthree four", TextWrap.wrap("one two three four", 10));
    }

    @Test
    void noLineIsLongerThanTheWidth() {
        String wrapped = TextWrap.wrap("the quick brown fox jumps over the lazy dog again and again", 12);
        for (String line : wrapped.split("\n")) {
            assertTrue(line.length() <= 12, "too long: " + line);
        }
    }

    @Test
    void keepsLineBreaksThatWereAlreadyThere() {
        assertEquals("Title\n\nBody text", TextWrap.wrap("Title\n\nBody text", 20));
    }

    @Test
    void keepsIndentationOfIndentedLines() {
        assertEquals("  help   Show help.", TextWrap.wrap("  help   Show help.", 40));
    }

    @Test
    void wrappedIndentedLineKeepsItsIndentOnEveryLine() {
        assertEquals("  one two\n  three", TextWrap.wrap("  one two three", 10));
    }

    @Test
    void rejectsNullTextAndNonPositiveWidth() {
        assertThrows(NullPointerException.class, () -> TextWrap.wrap(null, 10));
        assertThrows(IllegalArgumentException.class, () -> TextWrap.wrap("text", 0));
    }

    @Test
    void wordLongerThanWidthStaysWhole() {
        assertEquals("a\nextraordinarily\nb", TextWrap.wrap("a extraordinarily b", 5));
    }

    @Test
    void emptyTextStaysEmpty() {
        assertEquals("", TextWrap.wrap("", 10));
    }
}
