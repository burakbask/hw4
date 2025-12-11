package game;

/**
 * SlidingPuzzleApp is the main entry point for the Sliding Penguins Puzzle Game.
 * This application simulates a puzzle game where 3 penguins compete to collect
 * the most food (by weight) while sliding on icy terrain and avoiding hazards.
 *
 * The main method simply initializes an IcyTerrain object, which handles
 * all game setup, gameplay, and result display.
 *
 * @author CENG211 Programming Fundamentals
 * @version Homework #3
 */
public class SlidingPuzzleApp {

    /**
     * Main method - entry point of the application.
     * Initializes the IcyTerrain object which sets up and runs the game.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize IcyTerrain - this starts the entire game
        new IcyTerrain();
    }
}
