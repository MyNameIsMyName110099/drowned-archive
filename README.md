# The Drowned Archive

A text-based adventure game that runs in the console, written in Java for my Java 2 final project.

You play as a mermaid archaeologist. Your people are dying and nobody knows why, so you
dive into ancient sunken ruins to find out what happened. Each ruin is a grid of chambers
to explore, with murals, tablets and relics that slowly reveal the story.

## Current progress
- [x] Verb-noun command parser (`swim north`, `look at the mural`, `examine tablet`)
- [x] `help` command and friendly messages for commands the game doesn't understand
- [x] First ruin, **The Coral Gate**: 7 chambers on a 2D array map
- [x] Swimming between chambers, looking around and examining things
- [ ] Items and inventory (`take`, `use`), artifacts and a journal
- [ ] Sea creatures, combat and a corrupted boss at the end of each ruin
- [ ] More ruins, the final reveal and endings
- [ ] Save system

## How to play
| Command | What it does |
|---|---|
| `swim <direction>` | Swim north, south, east or west. Just `n`, `s`, `e`, `w` works too. |
| `look` | Describe the chamber you're in. |
| `look <thing>` / `examine <thing>` | Take a closer look at something in the chamber. |
| `help` | List the commands. |
| `quit` | Leave the game. |

## Running it
**Eclipse:** File > Import > General > Existing Projects into Workspace, pick this folder,
then right-click `src/drownedarchive/Main.java` > Run As > Java Application.

**Command line** (from this folder):
```
javac -d out -sourcepath src src/drownedarchive/Main.java
java -cp out drownedarchive.Main
```

## Tests
The tests use JUnit 5 (included with Eclipse). In Eclipse, right-click the `test` folder >
Run As > JUnit Test.

## Project structure
```
src/drownedarchive/
  Main.java             starts the game
  GameEngine.java       game loop; turns each command into what happens next
  TextWrap.java         wraps long text to fit the console
  command/              the verb-noun parser (Verb, Command, CommandParser)
  world/                the map (Direction, Ruin with its Chamber inner class, RuinLibrary)
  character/            the player
test/drownedarchive/    JUnit tests, same layout as src
```

All of the story text for the ruins lives in `world/RuinLibrary.java`.

Code sticks to Java 8 features to match the course textbook.
