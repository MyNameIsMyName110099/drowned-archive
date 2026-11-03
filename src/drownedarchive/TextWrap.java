package drownedarchive;

import java.util.Objects;

/*
 * Breaks long lines between words so descriptions fit in a narrow console
 * (Eclipse's console is small). Line breaks that are already in the text are kept,
 * and an indented line stays indented after it's wrapped.
 */
public final class TextWrap {

    private TextWrap() {
        // only static helpers
    }

    public static String wrap(String text, int width) {
        Objects.requireNonNull(text, "text");
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive, got " + width);
        }
        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                result.append('\n');
            }
            result.append(lines[i].length() <= width ? lines[i] : wrapLine(lines[i], width));
        }
        return result.toString();
    }

    private static String wrapLine(String line, int width) {
        String indent = line.substring(0, line.length() - line.replaceAll("^\\s+", "").length());
        StringBuilder wrapped = new StringBuilder(indent);
        int lineLength = indent.length();
        for (String word : line.trim().split("\\s+")) {
            if (lineLength > indent.length() && lineLength + 1 + word.length() > width) {
                wrapped.append('\n').append(indent);
                lineLength = indent.length();
            } else if (lineLength > indent.length()) {
                wrapped.append(' ');
                lineLength++;
            }
            // A single word longer than the width goes on its own line rather than being cut.
            wrapped.append(word);
            lineLength += word.length();
        }
        return wrapped.toString();
    }
}
