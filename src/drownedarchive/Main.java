package drownedarchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import drownedarchive.world.RuinLibrary;

/* Starts the game in the console. */
public class Main {

    public static void main(String[] args) {
        GameEngine game = new GameEngine(RuinLibrary.coralGate());
        try {
            game.run(new BufferedReader(new InputStreamReader(System.in)), System.out);
        } catch (IOException e) {
            System.err.println("The game couldn't read your input and has to stop: " + e.getMessage());
            System.exit(1);
        }
    }
}
