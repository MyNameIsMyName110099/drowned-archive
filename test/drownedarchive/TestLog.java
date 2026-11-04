package drownedarchive;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/* Lets tests see what was added to the game's log file, ignoring what earlier runs left there. */
final class TestLog {
    static final Path FILE = Paths.get("logs", "drowned-archive.log");

    private TestLog() {
    }

    static String writtenDuring(Runnable action) throws IOException {
        long before = Files.exists(FILE) ? Files.size(FILE) : 0;
        action.run();
        try (RandomAccessFile file = new RandomAccessFile(FILE.toFile(), "r")) {
            file.seek(before);
            byte[] added = new byte[(int) (file.length() - before)];
            file.readFully(added);
            return new String(added, StandardCharsets.UTF_8);
        }
    }
}
