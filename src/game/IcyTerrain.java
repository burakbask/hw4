package game;

import interfaces.ITerrainObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import objects.*;
import interfaces.IHazard;
import enums.Direction;

/**
 * IcyTerrain represents the 10x10 game grid and manages all game logic.
 * This class handles:
 * - Grid initialization and object placement
 * - Penguin, hazard, and food generation
 * - Sliding physics and collision detection
 * - Turn-based gameplay and player input
 * - Scoreboard and game result calculation
 *
 * The game consists of 3 penguins competing to collect the most food (by weight)
 * over 4 turns while navigating hazards and utilizing special abilities.
 */
public class IcyTerrain {

    // ============================================================================
    // CONSTANTS
    // ============================================================================
    private static final int GRID_SIZE = 10;
    private static final int MAX_TURNS = 4;
    private static final int PENGUIN_COUNT = 3;
    private static final int HAZARD_COUNT = 15;
    private static final int FOOD_COUNT = 20;
    private static final int AI_ABILITY_CHANCE = 30; // 30% chance for AI to use special ability

    // ============================================================================
    // INSTANCE VARIABLES
    // ============================================================================
    private List<List<ITerrainObject>> map; // The 10x10 grid
    private List<Penguin> penguins; // All penguins in the game
    private Penguin playerPenguin; // The player's penguin (always P2)
    private Scanner scanner; // For player input
    private Random random; // For random generation

    // ============================================================================
    // CONSTRUCTOR
    // ============================================================================

    /**
     * Creates a new IcyTerrain and initializes the game.
     * This constructor sets up the grid, generates all game objects,
     * and starts the gameplay loop.
     */
    public IcyTerrain() {
        // Initialize grid as 10x10 null grid
        map = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            List<ITerrainObject> row = new ArrayList<>();
            for (int j = 0; j < GRID_SIZE; j++) {
                row.add(null);
            }
            map.add(row);
        }

        penguins = new ArrayList<>();
        scanner = new Scanner(System.in);
        random = new Random();

        // Setup and start the game
        setupGame();
        startGame();
    }

    // ============================================================================
    // GAME SETUP METHODS
    // ============================================================================

    /**
     * Sets up the initial game state by generating penguins, hazards, and food.
     * Displays the initial grid and penguin information.
     */
    private void setupGame() {
        System.out.println("Welcome to Sliding Penguins Puzzle Game App. An " + GRID_SIZE + "x" + GRID_SIZE + " icy terrain grid is being generated.");
        System.out.println("Penguins, Hazards, and Food items are also being generated.");

        generatePenguins();
        placePenguinsOnGrid();
        generateHazards();
        generateFood();

        System.out.println("The initial icy terrain grid:");
        printTerrain();

        printPenguinInfo();
    }

    /**
     * Generates 3 random penguins with names P1, P2, P3.
     * P2 is always assigned as the player's penguin.
     * Each penguin has an equal 25% chance of being any of the 4 types.
     */
    private void generatePenguins() {
        for (int i = 1; i <= PENGUIN_COUNT; i++) {
            String name = "P" + i;
            int type = random.nextInt(4); // 0-3 for four penguin types
            Penguin p = null;

            switch(type) {
                case 0: p = new KingPenguin(name); break;
                case 1: p = new EmperorPenguin(name); break;
                case 2: p = new RoyalPenguin(name); break;
                case 3: p = new RockhopperPenguin(name); break;
            }

            penguins.add(p);
        }

        // P2 (index 1) is always the player's penguin
        playerPenguin = penguins.get(1);
    }

    /**
     * Places all penguins on edge squares of the grid randomly.
     * No two penguins can occupy the same square.
     */
    private void placePenguinsOnGrid() {
        for (Penguin p : penguins) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(GRID_SIZE);
                int col = random.nextInt(GRID_SIZE);

                // Check if position is on the edge
                boolean isEdge = (row == 0 || row == GRID_SIZE-1 || col == 0 || col == GRID_SIZE-1);

                if (isEdge && isEmpty(row, col)) {
                    placeObjectAt(row, col, p);
                    placed = true;
                }
            }
        }
    }

    /**
     * Generates 15 random hazards and places them on the grid.
     * Hazards cannot occupy the same space as penguins.
     * Each hazard type has an equal 25% chance of being generated.
     */
    private void generateHazards() {
        int count = 0;
        while (count < HAZARD_COUNT) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if (isEmpty(row, col)) {
                int type = random.nextInt(4); // 0-3 for four hazard types
                IHazard hazard = null;

                switch(type) {
                    case 0: hazard = new LightIceBlock(); break;
                    case 1: hazard = new HeavyIceBlock(); break;
                    case 2: hazard = new SeaLion(); break;
                    case 3: hazard = new HoleInIce(); break;
                }

                placeObjectAt(row, col, hazard);
                count++;
            }
        }
    }

    /**
     * Generates 20 random food items and places them on the grid.
     * Food cannot occupy spaces with penguins or hazards.
     * Each food gets a random type and weight (1-5).
     */
    private void generateFood() {
        int count = 0;
        while (count < FOOD_COUNT) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if (isEmpty(row, col)) {
                placeObjectAt(row, col, new Food());
                count++;
            }
        }
    }

    /**
     * Prints information about all penguins in the game.
     * Shows penguin number, name, type, and indicates which is the player's penguin.
     */
    private void printPenguinInfo() {
        System.out.println("These are the penguins on the icy terrain:");
        int count = 1;
        for (Penguin p : penguins) {
            String role = "";
            if (p == playerPenguin) {
                role = " ---> YOUR PENGUIN";
            }

            // Convert class name to readable format (e.g., "KingPenguin" -> "King Penguin")
            String className = p.getClass().getSimpleName();
            String formattedName = className.replaceAll("(?<=\\p{Ll})(?=\\p{Lu})", " ");

            System.out.println("- Penguin " + count + " (" + p.getSymbol() + "): " + formattedName + role);
            count++;
        }
        System.out.println();
    }

    // ============================================================================
    // GAMEPLAY LOOP
    // ============================================================================

    /**
     * Main gameplay loop that runs for 4 turns.
     * Each turn, all active penguins make their moves in order (P1, P2, P3).
     * Eliminated or stunned penguins skip their turns.
     */
    private void startGame() {
        for (int turn = 1; turn <= MAX_TURNS; turn++) {
            for (Penguin p : penguins) {
                // Skip eliminated penguins
                if (!p.isActive()) {
                    continue;
                }

                // Check if penguin is stunned from previous turn
                if (p.isStunned()) {
                    String role = (p == playerPenguin) ? " (Your Penguin)" : "";
                    System.out.println("*** Turn " + turn + " - " + p.getSymbol() + role + ":");
                    System.out.println(p.getSymbol() + " is stunned and skips this turn!");
                    p.clearStun(); // Clear stun for next turn
                    System.out.println("New state of the grid:");
                    printTerrain();
                    System.out.println();
                    continue;
                }

                // Display turn header
                String role = (p == playerPenguin) ? " (Your Penguin)" : "";
                System.out.println("*** Turn " + turn + " - " + p.getSymbol() + role + ":");

                // Handle turn based on whether it's player or AI
                if (p == playerPenguin) {
                    handlePlayerTurn(p);
                } else {
                    handleAITurn(p);
                }

                // Display updated grid
                System.out.println("New state of the grid:");
                printTerrain();
                System.out.println();
            }
        }

        // Game over - display results
        System.out.println("***** GAME OVER *****");
        printScoreboard();
    }

    // ============================================================================
    // TURN HANDLING
    // ============================================================================

    /**
     * Handles a player's turn by prompting for input.
     * Asks whether to use special ability and which direction to move.
     * @param p The player's penguin
     */
    private void handlePlayerTurn(Penguin p) {
        // Ask if player wants to use special ability (only if not already used)
        boolean useAbility = false;
        if (!p.hasUsedSpecialAbility()) {
            String answer;
            do {
                System.out.print("Will " + p.getSymbol() + " use its special action? Answer with Y or N --> ");
                answer = scanner.next().trim().toUpperCase();
            } while(!answer.equals("Y") && !answer.equals("N"));

            useAbility = answer.equals("Y");
        }

        // Ask for movement direction
        String dirInput;
        do {
            System.out.print("Which direction will " + p.getSymbol() + " move? Answer with U (Up), D (Down), L (Left), R (Right) --> ");
            dirInput = scanner.next().trim().toUpperCase();
        } while(!dirInput.equals("U") && !dirInput.equals("D") && !dirInput.equals("L") && !dirInput.equals("R"));

        Direction dir = parseDirection(dirInput);

        // Execute move
        if (useAbility) {
            executeSpecialAbility(p, dir);
        } else {
            simulateSlide(p, dir);
        }
    }

    /**
     * Handles an AI penguin's turn with intelligent decision making.
     * AI prioritizes: Food > Hazards (to stop safely) > Random direction
     * AI has a 30% chance of using special ability each turn.
     * Exception: RockhopperPenguin auto-uses ability when moving toward hazard.
     * @param p The AI penguin
     */
    private void handleAITurn(Penguin p) {
        // Decide whether to use special ability (30% chance, except for Rockhopper special case)
        boolean useAbility = false;
        Direction chosenDirection = null;

        if (!p.hasUsedSpecialAbility() && random.nextInt(100) < AI_ABILITY_CHANCE) {
            useAbility = true;
        }

        // Choose direction based on AI logic
        chosenDirection = chooseAIDirection(p);

        // Special case: RockhopperPenguin auto-uses ability when moving toward hazard
        if (p instanceof RockhopperPenguin && !p.hasUsedSpecialAbility()) {
            if (hasHazardInDirection(p, chosenDirection)) {
                useAbility = true;
                System.out.println(p.getSymbol() + " will automatically USE its special action.");
            }
        }

        // Display AI decision
        if (useAbility && !p.hasUsedSpecialAbility()) {
            System.out.println(p.getSymbol() + " chooses to USE its special action.");
        } else if (!p.hasUsedSpecialAbility()) {
            System.out.println(p.getSymbol() + " does NOT use its special action.");
        }

        System.out.println(p.getSymbol() + " chooses to move " + directionToString(chosenDirection) + ".");

        // Execute move
        if (useAbility && !p.hasUsedSpecialAbility()) {
            executeSpecialAbility(p, chosenDirection);
        } else {
            simulateSlide(p, chosenDirection);
        }
    }

    /**
     * AI decision making: chooses the best direction to move.
     * Priority: Food > Hazard (to stop) > Random
     * @param p The penguin making the decision
     * @return The chosen direction
     */
    private Direction chooseAIDirection(Penguin p) {
        Direction[] directions = Direction.values();
        List<Direction> directionsWithFood = new ArrayList<>();
        List<Direction> directionsWithHazard = new ArrayList<>();

        int[] pos = getPosition(p);
        if (pos == null) return directions[0]; // Fallback

        // Scan all directions for food and hazards
        for (Direction d : directions) {
            scanDirectionForObjects(p, d, directionsWithFood, directionsWithHazard);
        }

        // Choose best direction based on priority
        if (!directionsWithFood.isEmpty()) {
            return directionsWithFood.get(random.nextInt(directionsWithFood.size()));
        } else if (!directionsWithHazard.isEmpty()) {
            return directionsWithHazard.get(random.nextInt(directionsWithHazard.size()));
        } else {
            return directions[random.nextInt(directions.length)];
        }
    }

    /**
     * Scans a direction for food and hazards, populating the given lists.
     * @param p The penguin scanning
     * @param dir The direction to scan
     * @param foodDirs List to add direction if food is found
     * @param hazardDirs List to add direction if hazard is found
     */
    private void scanDirectionForObjects(Penguin p, Direction dir, List<Direction> foodDirs, List<Direction> hazardDirs) {
        int[] pos = getPosition(p);
        if (pos == null) return;

        int[] delta = getDirectionDelta(dir);
        int row = pos[0];
        int col = pos[1];

        // Scan along the direction until hitting something or edge
        while (true) {
            row += delta[0];
            col += delta[1];

            if (isOutOfBounds(row, col)) break;

            ITerrainObject obj = getObjectAt(row, col);
            if (obj == null) continue; // Empty space, keep scanning

            if (obj instanceof Food) {
                foodDirs.add(dir);
                break;
            } else if (obj instanceof IHazard || obj instanceof Penguin) {
                hazardDirs.add(dir);
                break;
            }
        }
    }

    /**
     * Checks if there's a hazard in the given direction (for Rockhopper AI).
     * @param p The penguin checking
     * @param dir The direction to check
     * @return true if a hazard exists in that direction
     */
    private boolean hasHazardInDirection(Penguin p, Direction dir) {
        int[] pos = getPosition(p);
        if (pos == null) return false;

        int[] delta = getDirectionDelta(dir);
        int row = pos[0];
        int col = pos[1];

        while (true) {
            row += delta[0];
            col += delta[1];

            if (isOutOfBounds(row, col)) break;

            ITerrainObject obj = getObjectAt(row, col);
            if (obj instanceof IHazard) return true;
            if (obj instanceof Penguin || obj instanceof Food) break;
        }

        return false;
    }

    // ============================================================================
    // SPECIAL ABILITIES
    // ============================================================================

    /**
     * Executes a penguin's special ability based on its type.
     * @param p The penguin using the ability
     * @param dir The direction to move
     */
    private void executeSpecialAbility(Penguin p, Direction dir) {
        p.useSpecialAbility(); // Mark ability as used

        if (p instanceof KingPenguin) {
            System.out.println(p.getSymbol() + " uses King Ability (stops at 5th square).");
            simulateSlideWithLimit(p, dir, 5);
        } else if (p instanceof EmperorPenguin) {
            System.out.println(p.getSymbol() + " uses Emperor Ability (stops at 3rd square).");
            simulateSlideWithLimit(p, dir, 3);
        } else if (p instanceof RoyalPenguin) {
            executeRoyalAbility(p, dir);
        } else if (p instanceof RockhopperPenguin) {
            executeRockhopperAbility(p, dir);
        }
    }

    /**
     * RoyalPenguin ability: Move 1 square safely, then slide normally from new position.
     * @param p The RoyalPenguin
     * @param dir The direction to move
     */
    private void executeRoyalAbility(Penguin p, Direction dir) {
        System.out.println(p.getSymbol() + " moves 1 square (Royal Walk).");

        int[] pos = getPosition(p);
        if (pos == null) return;

        int[] delta = getDirectionDelta(dir);
        int nextRow = pos[0] + delta[0];
        int nextCol = pos[1] + delta[1];

        // Check if stepping out of bounds
        if (isOutOfBounds(nextRow, nextCol)) {
            System.out.println(p.getSymbol() + " stepped out of the grid!");
            p.eliminate();
            clearCell(pos[0], pos[1]);
            return;
        }

        ITerrainObject nextObj = getObjectAt(nextRow, nextCol);

        // Handle the single step movement
        if (nextObj == null) {
            moveObjectAtomic(pos[0], pos[1], nextRow, nextCol);
        } else if (nextObj instanceof Food) {
            moveObjectAtomic(pos[0], pos[1], nextRow, nextCol);
            p.eatFood((Food) nextObj);
            return; // Stop after eating food
        } else {
            System.out.println(p.getSymbol() + " cannot step onto " + nextObj.getSymbol() + "!");
            return;
        }

        // Now slide normally from new position
        System.out.println(p.getSymbol() + " now slides from the new position.");
        simulateSlide(p, dir);
    }

    /**
     * RockhopperPenguin ability: Jump over one hazard in path.
     * Can only jump to empty square or square with food.
     * @param p The RockhopperPenguin
     * @param dir The direction to jump/move
     */
    private void executeRockhopperAbility(Penguin p, Direction dir) {
        System.out.println(p.getSymbol() + " prepares to jump over a hazard.");

        int[] pos = getPosition(p);
        if (pos == null) return;

        int[] delta = getDirectionDelta(dir);

        // Find first hazard in path
        ITerrainObject hazard = null;
        int hazardRow = -1, hazardCol = -1;

        int row = pos[0];
        int col = pos[1];

        for (int dist = 1; dist < GRID_SIZE * 2; dist++) {
            row = pos[0] + delta[0] * dist;
            col = pos[1] + delta[1] * dist;

            if (isOutOfBounds(row, col)) break;

            ITerrainObject obj = getObjectAt(row, col);
            if (obj instanceof IHazard) {
                hazard = obj;
                hazardRow = row;
                hazardCol = col;
                break;
            } else if (obj instanceof Food || obj instanceof Penguin) {
                // Food or penguin blocks the search
                break;
            }
        }

        // If no hazard found, slide normally
        if (hazard == null) {
            System.out.println("No hazard to jump over. " + p.getSymbol() + " slides normally.");
            simulateSlide(p, dir);
            return;
        }

        // Try to jump over hazard
        int landRow = hazardRow + delta[0];
        int landCol = hazardCol + delta[1];

        if (isOutOfBounds(landRow, landCol)) {
            System.out.println(p.getSymbol() + " jumps over " + hazard.getSymbol() + " but lands in water!");
            p.eliminate();
            clearCell(pos[0], pos[1]);
            return;
        }

        ITerrainObject landingSpot = getObjectAt(landRow, landCol);

        // Can only land on empty or food
        if (landingSpot == null || landingSpot instanceof Food) {
            System.out.println(p.getSymbol() + " jumps over " + hazard.getSymbol() + "!");
            moveObjectAtomic(pos[0], pos[1], landRow, landCol);
            if (landingSpot instanceof Food) {
                p.eatFood((Food) landingSpot);
            }
        } else {
            System.out.println("Jump failed! Landing spot occupied by " + landingSpot.getSymbol() + ".");
            System.out.println(p.getSymbol() + " slides normally instead.");
            simulateSlide(p, dir);
        }
    }

    // ============================================================================
    // SLIDING PHYSICS ENGINE
    // ============================================================================

    /**
     * Simulates unlimited sliding until collision or falling off edge.
     * Handles all collision mechanics including hazards, food, and other penguins.
     * @param obj The object that is sliding
     * @param dir The direction of sliding
     */
    private void simulateSlide(ITerrainObject obj, Direction dir) {
        int[] delta = getDirectionDelta(dir);
        boolean sliding = true;

        while (sliding) {
            int[] pos = getPosition(obj);
            if (pos == null) break; // Object removed from grid

            int currRow = pos[0];
            int currCol = pos[1];
            int nextRow = currRow + delta[0];
            int nextCol = currCol + delta[1];

            // CASE 1: Falling into water
            if (isOutOfBounds(nextRow, nextCol)) {
                System.out.println(obj.getSymbol() + " fell into the water!");
                handleElimination(obj);
                clearCell(currRow, currCol);
                return;
            }

            ITerrainObject nextObj = getObjectAt(nextRow, nextCol);

            // CASE 2: Empty space - keep sliding
            if (nextObj == null) {
                moveObjectAtomic(currRow, currCol, nextRow, nextCol);
            }
            // CASE 3: Food - collect and stop (for penguins) or crush (for hazards)
            else if (nextObj instanceof Food) {
                if (obj instanceof Penguin) {
                    moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                    ((Penguin) obj).eatFood((Food) nextObj);
                    return; // Penguins stop at food
                } else {
                    // Hazards crush food and continue sliding
                    System.out.println(obj.getSymbol() + " crushed " + nextObj.getSymbol() + "!");
                    moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                }
            }
            // CASE 4: Collision with obstacle
            else {
                handleCollision(obj, nextObj, dir, currRow, currCol, nextRow, nextCol);
                return;
            }
        }
    }

    /**
     * Simulates sliding with a maximum step limit (for King/Emperor abilities).
     * Stops early if maxSteps is reached.
     * @param obj The object sliding
     * @param dir The direction
     * @param maxSteps Maximum number of squares to slide
     */
    private void simulateSlideWithLimit(ITerrainObject obj, Direction dir, int maxSteps) {
        int[] delta = getDirectionDelta(dir);
        int stepsTaken = 0;

        while (stepsTaken < maxSteps) {
            int[] pos = getPosition(obj);
            if (pos == null) return;

            int currRow = pos[0];
            int currCol = pos[1];
            int nextRow = currRow + delta[0];
            int nextCol = currCol + delta[1];

            // Check boundaries
            if (isOutOfBounds(nextRow, nextCol)) {
                System.out.println(obj.getSymbol() + " fell into the water!");
                handleElimination(obj);
                clearCell(currRow, currCol);
                return;
            }

            ITerrainObject nextObj = getObjectAt(nextRow, nextCol);

            // Empty space - move and increment counter
            if (nextObj == null) {
                moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                stepsTaken++;
            }
            // Food - collect and stop
            else if (nextObj instanceof Food && obj instanceof Penguin) {
                moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                ((Penguin) obj).eatFood((Food) nextObj);
                return;
            }
            // Obstacle - handle collision and stop
            else {
                handleCollision(obj, nextObj, dir, currRow, currCol, nextRow, nextCol);
                return;
            }
        }

        // Reached step limit
        if (stepsTaken == maxSteps) {
            System.out.println(obj.getSymbol() + " stopped early using special ability.");
        }
    }

    // ============================================================================
    // COLLISION HANDLING
    // ============================================================================

    /**
     * Handles collision between a moving object and a stationary object.
     * Implements all special collision mechanics:
     * - HoleInIce: Object falls in (penguins eliminated, hazards plug hole)
     * - HeavyIceBlock: Penguin loses lightest food
     * - LightIceBlock: Penguin gets stunned, block slides away
     * - SeaLion: Penguin bounces back, sea lion slides away
     * - Penguin-to-Penguin: Moving stops, stationary starts sliding
     *
     * @param moving The object that is sliding
     * @param stationary The object being hit
     * @param dir The direction of movement
     * @param movingRow Current row of moving object
     * @param movingCol Current column of moving object
     * @param stationaryRow Row of stationary object
     * @param stationaryCol Column of stationary object
     */
    private void handleCollision(ITerrainObject moving, ITerrainObject stationary, Direction dir,
                                 int movingRow, int movingCol, int stationaryRow, int stationaryCol) {
        System.out.println(moving.getSymbol() + " hit " + stationary.getSymbol() + "!");

        // Collision with HoleInIce
        if (stationary instanceof HoleInIce) {
            handleHoleCollision(moving, (HoleInIce) stationary, movingRow, movingCol);
            return;
        }

        // Collision with HeavyIceBlock
        if (stationary instanceof HeavyIceBlock) {
            if (moving instanceof Penguin) {
                Penguin p = (Penguin) moving;
                boolean foodLost = p.removeLightestFood();
                if (!foodLost) {
                    System.out.println(p.getSymbol() + " has no food to lose!");
                }
            }
            return; // Moving object stops
        }

        // Collision with LightIceBlock
        if (stationary instanceof LightIceBlock) {
            if (moving instanceof Penguin) {
                ((Penguin) moving).stun(); // Stun the penguin
            }
            // Push the light ice block
            simulateSlide(stationary, dir);
            return;
        }

        // Collision with SeaLion
        if (stationary instanceof SeaLion) {
            handleSeaLionCollision(moving, stationary, dir);
            return;
        }

        // Collision with another Penguin
        if (stationary instanceof Penguin && moving instanceof Penguin) {
            handlePenguinCollision((Penguin) moving, (Penguin) stationary, dir);
            return;
        }

        // Default: just stop (for any other obstacles)
    }

    /**
     * Handles collision with a HoleInIce.
     * Penguins are eliminated, hazards plug the hole.
     * Plugged holes can be passed through.
     */
    private void handleHoleCollision(ITerrainObject obj, HoleInIce hole, int row, int col) {
        if (!hole.isPlugged()) {
            System.out.println(obj.getSymbol() + " fell into the hole!");
            handleElimination(obj);
            clearCell(row, col);

            // Non-penguins plug the hole
            if (!(obj instanceof Penguin)) {
                hole.setPlugged(true);
                System.out.println("The hole is now PLUGGED.");
            }
        }
        // If hole is plugged, it doesn't stop the object
        // (but this case shouldn't happen as plugged holes are passable)
    }

    /**
     * Handles collision with a SeaLion (bounce mechanic).
     * Sea lion slides in same direction, penguin bounces back in opposite direction.
     */
    private void handleSeaLionCollision(ITerrainObject moving, ITerrainObject seaLion, Direction dir) {
        System.out.println("BOING! " + seaLion.getSymbol() + " bounces away!");

        // SeaLion slides in same direction as the moving object
        simulateSlide(seaLion, dir);

        // If moving object is a penguin, it bounces back in opposite direction
        if (moving instanceof Penguin) {
            Direction opposite = getOppositeDirection(dir);
            simulateSlide(moving, opposite);
        }
        // If moving object is a hazard, it just stops (doesn't bounce)
    }

    /**
     * Handles penguin-to-penguin collision.
     * The moving penguin stops at its current position.
     * The stationary penguin starts sliding in the same direction.
     */
    private void handlePenguinCollision(Penguin moving, Penguin stationary, Direction dir) {
        System.out.println("Penguin collision! " + stationary.getSymbol() + " starts sliding!");
        // Moving penguin stops (already in position before collision)
        // Stationary penguin starts sliding in the same direction
        simulateSlide(stationary, dir);
    }

    /**
     * Handles object elimination (penguins falling into water/holes).
     * @param obj The object being eliminated
     */
    private void handleElimination(ITerrainObject obj) {
        if (obj instanceof Penguin) {
            ((Penguin) obj).eliminate();
        }
    }

    // ============================================================================
    // GRID HELPER METHODS
    // ============================================================================

    /**
     * Gets the object at the specified grid position.
     * @param row The row index
     * @param col The column index
     * @return The object at that position, or null if empty/out of bounds
     */
    private ITerrainObject getObjectAt(int row, int col) {
        if (isOutOfBounds(row, col)) return null;
        return map.get(row).get(col);
    }

    /**
     * Places an object at the specified grid position.
     * @param row The row index
     * @param col The column index
     * @param obj The object to place
     */
    private void placeObjectAt(int row, int col, ITerrainObject obj) {
        if (!isOutOfBounds(row, col)) {
            map.get(row).set(col, obj);
        }
    }

    /**
     * Checks if coordinates are outside the grid (water).
     * @param row The row index
     * @param col The column index
     * @return true if out of bounds, false otherwise
     */
    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE;
    }

    /**
     * Checks if a grid position is empty (no object).
     * @param row The row index
     * @param col The column index
     * @return true if empty and in bounds, false otherwise
     */
    private boolean isEmpty(int row, int col) {
        return !isOutOfBounds(row, col) && getObjectAt(row, col) == null;
    }

    /**
     * Finds the position of an object on the grid.
     * @param obj The object to find
     * @return [row, col] array, or null if not found
     */
    private int[] getPosition(ITerrainObject obj) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (map.get(i).get(j) == obj) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    /**
     * Clears a grid cell (sets it to null).
     * @param row The row index
     * @param col The column index
     */
    private void clearCell(int row, int col) {
        if (!isOutOfBounds(row, col)) {
            map.get(row).set(col, null);
        }
    }

    /**
     * Moves an object from one position to another atomically.
     * The destination position is overwritten.
     * @param fromRow Source row
     * @param fromCol Source column
     * @param toRow Destination row
     * @param toCol Destination column
     */
    private void moveObjectAtomic(int fromRow, int fromCol, int toRow, int toCol) {
        ITerrainObject obj = getObjectAt(fromRow, fromCol);
        placeObjectAt(toRow, toCol, obj);
        clearCell(fromRow, fromCol);
    }

    // ============================================================================
    // DIRECTION HELPERS
    // ============================================================================

    /**
     * Converts a direction to row/column delta.
     * @param dir The direction
     * @return [dRow, dCol] array
     */
    private int[] getDirectionDelta(Direction dir) {
        switch (dir) {
            case UP:    return new int[]{-1, 0};
            case DOWN:  return new int[]{1, 0};
            case LEFT:  return new int[]{0, -1};
            case RIGHT: return new int[]{0, 1};
            default:    return new int[]{0, 0};
        }
    }

    /**
     * Gets the opposite direction.
     * @param dir The original direction
     * @return The opposite direction
     */
    private Direction getOppositeDirection(Direction dir) {
        switch (dir) {
            case UP:    return Direction.DOWN;
            case DOWN:  return Direction.UP;
            case LEFT:  return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
            default:    return Direction.UP;
        }
    }

    /**
     * Parses direction from user input string.
     * @param input The input string (U/D/L/R)
     * @return The corresponding Direction enum value
     */
    private Direction parseDirection(String input) {
        switch(input.toUpperCase()) {
            case "U": return Direction.UP;
            case "D": return Direction.DOWN;
            case "L": return Direction.LEFT;
            case "R": return Direction.RIGHT;
            default: return Direction.UP;
        }
    }

    /**
     * Converts direction to readable string for display.
     * @param dir The direction
     * @return A display string (e.g., "UPWARDS", "to the LEFT")
     */
    private String directionToString(Direction dir) {
        switch(dir) {
            case UP:    return "UPWARDS";
            case DOWN:  return "DOWNWARDS";
            case LEFT:  return "to the LEFT";
            case RIGHT: return "to the RIGHT";
            default:    return "UPWARDS";
        }
    }

    // ============================================================================
    // DISPLAY METHODS
    // ============================================================================

    /**
     * Prints the current state of the 10x10 grid with borders.
     * Empty cells show as spaces, objects show their 2-3 character symbols.
     */
    private void printTerrain() {
        System.out.println("-------------------------------------------------------------");
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print("| ");
            for (int j = 0; j < GRID_SIZE; j++) {
                ITerrainObject obj = map.get(i).get(j);

                if (obj == null) {
                    System.out.print("    "); // Empty cell
                } else {
                    String sym = obj.getSymbol();
                    System.out.print(String.format("%-3s ", sym)); // Left-aligned, 3 chars wide
                }
                System.out.print("| ");
            }
            System.out.println();
            System.out.println("-------------------------------------------------------------");
        }
    }

    /**
     * Prints the final scoreboard with rankings and detailed food information.
     * Shows each penguin's rank, collected food items (with weights), and total weight.
     */
    private void printScoreboard() {
        System.out.println("***** SCOREBOARD FOR THE PENGUINS *****");

        // Sort penguins by total weight (descending order)
        Collections.sort(penguins, new Comparator<Penguin>() {
            @Override
            public int compare(Penguin p1, Penguin p2) {
                return Integer.compare(p2.getTotalWeight(), p1.getTotalWeight());
            }
        });

        // Display rankings
        for (int rank = 0; rank < penguins.size(); rank++) {
            Penguin p = penguins.get(rank);
            String role = (p == playerPenguin) ? " (Your Penguin)" : "";
            String suffix = getSuffix(rank + 1);

            System.out.println("* " + (rank + 1) + suffix + " place: " + p.getSymbol() + role);

            // Display food items with weights
            List<Food> inventory = p.getInventory();
            if (inventory.isEmpty()) {
                System.out.println(" |---> Food items: None");
            } else {
                System.out.print(" |---> Food items: ");
                for (int i = 0; i < inventory.size(); i++) {
                    Food f = inventory.get(i);
                    System.out.print(f.getSymbol() + " (" + f.getWeight() + " units)");
                    if (i < inventory.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }

            // Display total weight
            System.out.println(" |---> Total weight: " + p.getTotalWeight() + " units");
        }
    }

    /**
     * Gets the ordinal suffix for a rank number.
     * @param rank The rank number (1, 2, 3, etc.)
     * @return The suffix ("st", "nd", "rd", or "th")
     */
    private String getSuffix(int rank) {
        if (rank == 1) return "st";
        if (rank == 2) return "nd";
        if (rank == 3) return "rd";
        return "th";
    }
}
