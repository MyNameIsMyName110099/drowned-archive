package drownedarchive.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/*
 * A ruin is one level of the game: a grid of chambers stored in a 2D array.
 * Row 0 is the north edge. Empty spots in the grid (null) are solid rock.
 *
 * The content in RuinLibrary is typed by hand, so every method here refuses bad
 * input straight away. A mistake shows up when the ruin is built, not halfway
 * through a playthrough.
 */
public class Ruin {
    private final String name;
    private final Chamber[][] chambers;
    private Chamber start;

    public Ruin(String name, int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("A ruin needs at least one row and column, got " + rows + "x" + cols);
        }
        this.name = requireText(name, "ruin name");
        this.chambers = new Chamber[rows][cols];
    }

    public String getName() {
        return name;
    }

    public Chamber addChamber(int row, int col, String title, String description) {
        if (!isInside(row, col)) {
            throw new IllegalArgumentException("(" + row + ", " + col + ") is outside " + name);
        }
        if (chambers[row][col] != null) {
            throw new IllegalStateException("There is already a chamber at (" + row + ", " + col + ") in " + name);
        }
        Chamber chamber = new Chamber(row, col, requireText(title, "chamber title"),
                requireText(description, "chamber description"));
        chambers[row][col] = chamber;
        return chamber;
    }

    public void setStart(Chamber chamber) {
        if (chamber.owner() != this) {
            throw new IllegalArgumentException(chamber + " is not part of " + name);
        }
        if (start != null) {
            throw new IllegalStateException(name + " already starts at " + start);
        }
        start = chamber;
    }

    public Chamber getStart() {
        if (start == null) {
            throw new IllegalStateException(name + " has no starting chamber");
        }
        return start;
    }

    /* Every chamber in the ruin, row by row. Handy for checking the map is complete. */
    public List<Chamber> allChambers() {
        List<Chamber> all = new ArrayList<>();
        for (Chamber[] row : chambers) {
            for (Chamber chamber : row) {
                if (chamber != null) {
                    all.add(chamber);
                }
            }
        }
        return Collections.unmodifiableList(all);
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < chambers.length && col >= 0 && col < chambers[row].length;
    }

    private static String requireText(String text, String what) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing " + what);
        }
        return text;
    }

    private static String normalise(String word) {
        return word.trim().toLowerCase(Locale.ROOT);
    }

    /*
     * Chamber is an inner class because a chamber only exists as part of a ruin,
     * and it needs the ruin's grid to find its neighbours.
     */
    public class Chamber {
        private final int row;
        private final int col;
        private final String title;
        private final String description;
        // Every name the player could type (main names and aliases) maps to the text.
        private final Map<String, String> featureText = new HashMap<>();
        // Main names only, in the order added, so "look" lists them naturally.
        private final List<String> featureNames = new ArrayList<>();

        private Chamber(int row, int col, String title, String description) {
            this.row = row;
            this.col = col;
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        /*
         * Aliases let the player type "tablet" for "tablets" - people don't think about
         * whether the room said singular or plural.
         */
        public void addFeature(String featureName, String text, String... aliases) {
            String mainName = normalise(requireText(featureName, "feature name"));
            requireText(text, "text for feature '" + featureName + "'");
            List<String> allNames = new ArrayList<>();
            allNames.add(mainName);
            for (String alias : aliases) {
                allNames.add(normalise(requireText(alias, "alias for '" + featureName + "'")));
            }
            for (String each : allNames) {
                if (featureText.containsKey(each)) {
                    throw new IllegalStateException(title + " already has a feature called '" + each + "'");
                }
            }
            for (String each : allNames) {
                featureText.put(each, text);
            }
            featureNames.add(mainName);
        }

        public Optional<String> feature(String featureName) {
            if (featureName == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(featureText.get(normalise(featureName)));
        }

        public List<String> featureNames() {
            return Collections.unmodifiableList(featureNames);
        }

        // Empty when there's a wall (edge of the grid) or solid rock that way.
        public Optional<Chamber> neighbour(Direction direction) {
            int r = row + direction.getRowChange();
            int c = col + direction.getColChange();
            return isInside(r, c) ? Optional.ofNullable(chambers[r][c]) : Optional.empty();
        }

        public List<Direction> exits() {
            List<Direction> open = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                if (neighbour(direction).isPresent()) {
                    open.add(direction);
                }
            }
            return Collections.unmodifiableList(open);
        }

        @Override
        public String toString() {
            return Ruin.this.name + " [" + row + "," + col + "]: " + title;
        }

        private Ruin owner() {
            return Ruin.this;
        }
    }
}
