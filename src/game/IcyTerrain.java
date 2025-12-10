package game;

import interfaces.ITerrainObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import objects.*;
import interfaces.IHazard;
import enums.Direction;

public class IcyTerrain {

    // 10x10 Grid
    private List<List<ITerrainObject>> map;

    public IcyTerrain() {
        // Initialize Grid
        map = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            List<ITerrainObject> row = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                row.add(null);
            }
            map.add(row);
        }
    }

    // --- HELPER METHODS ---

    public ITerrainObject getObjectAt(int row, int col) {
        if (isOutOfBounds(row, col)) return null;
        return map.get(row).get(col);
    }

    public void placeObjectAt(int row, int col, ITerrainObject obj) {
        if (!isOutOfBounds(row, col)) {
            map.get(row).set(col, obj);
        }
    }

    public boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= 10 || col < 0 || col >= 10;
    }

    public boolean isEmpty(int row, int col) {
        return !isOutOfBounds(row, col) && getObjectAt(row, col) == null;
    }

    public int[] getPosition(ITerrainObject obj) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (map.get(i).get(j) == obj) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public void clearCell(int row, int col) {
        if (!isOutOfBounds(row, col)) {
            map.get(row).set(col, null);
        }
    }

    // Teleport object from A to B (Internal helper)
    public void moveObjectAtomic(int fromRow, int fromCol, int toRow, int toCol) {
        ITerrainObject obj = getObjectAt(fromRow, fromCol);
        placeObjectAt(toRow, toCol, obj);
        clearCell(fromRow, fromCol);
    }

    // --- RENDER ---

    public void printTerrain() {
        System.out.println("-------------------------------------------------------------");
        for (int i = 0; i < 10; i++) {
            System.out.print("| ");
            for (int j = 0; j < 10; j++) {
                ITerrainObject obj = map.get(i).get(j);

                if (obj == null) {
                    System.out.print("    ");
                } else {
                    String sym = obj.getSymbol();
                    System.out.print(String.format("%-3s ", sym));
                }
                System.out.print("| ");
            }
            System.out.println();
            System.out.println("-------------------------------------------------------------");
        }
    }

    // --- SPAWNING ---

    public void generatePenguins(List<Penguin> penguins) {
        Random rand = new Random();
        for (Penguin p : penguins) {
            boolean placed = false;
            while (!placed) {
                int row = rand.nextInt(10);
                int col = rand.nextInt(10);

                boolean isEdge = (row == 0 || row == 9 || col == 0 || col == 9);

                if (isEdge && isEmpty(row, col)) {
                    placeObjectAt(row, col, p);
                    placed = true;
                }
            }
        }
    }

    public void generateHazards() {
        Random rand = new Random();
        int count = 0;

        while (count < 15) {
            int row = rand.nextInt(10);
            int col = rand.nextInt(10);

            if (isEmpty(row, col)) {
                int type = rand.nextInt(4);
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

    public void generateFood() {
        Random rand = new Random();
        int count = 0;

        while (count < 20) {
            int row = rand.nextInt(10);
            int col = rand.nextInt(10);

            if (isEmpty(row, col)) {
                placeObjectAt(row, col, new Food());
                count++;
            }
        }
    }

    // --- PHYSICS ENGINE (SLIDING LOGIC) ---

    public void simulateSlide(ITerrainObject obj, Direction dir) {
        // 1. Determine direction deltas
        int dRow = 0, dCol = 0;
        switch (dir) {
            case UP:    dRow = -1; break;
            case DOWN:  dRow = 1; break;
            case LEFT:  dCol = -1; break;
            case RIGHT: dCol = 1; break;
        }

        boolean sliding = true;

        while (sliding) {
            int[] pos = getPosition(obj);
            if (pos == null) break; // Object is gone

            int currRow = pos[0];
            int currCol = pos[1];

            int nextRow = currRow + dRow;
            int nextCol = currCol + dCol;

            // CASE 1: Falling into Water
            if (isOutOfBounds(nextRow, nextCol)) {
                System.out.println(obj.getSymbol() + " fell into the water!");
                handleElimination(obj);
                clearCell(currRow, currCol);
                sliding = false;
                return;
            }

            ITerrainObject nextObj = getObjectAt(nextRow, nextCol);

            // CASE 2: Empty Space -> Move
            if (nextObj == null) {
                moveObjectAtomic(currRow, currCol, nextRow, nextCol);
            }

            // CASE 3: Found Food -> Move & Eat (Only for Penguins)
            else if (nextObj instanceof Food) {
                if (obj instanceof Penguin) {
                    moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                    ((Penguin) obj).eatFood((Food) nextObj);
                    System.out.println(obj.getSymbol() + " collected food.");
                    sliding = false; // Penguins stop at food
                } else {
                    // Hazards destroy food and keep going!
                    System.out.println(obj.getSymbol() + " crushed " + nextObj.getSymbol());
                    moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                    // Sliding continues for hazard
                }
            }

            // CASE 4: Hitting an Obstacle
            else {
                System.out.println(obj.getSymbol() + " hit " + nextObj.getSymbol());

                // 4a. Hitting a HoleInIce
                if (nextObj instanceof HoleInIce) {
                    HoleInIce hole = (HoleInIce) nextObj;
                    if (!hole.isPlugged()) {
                        System.out.println(obj.getSymbol() + " fell into the hole!");
                        handleElimination(obj); // Remove the sliding object
                        clearCell(currRow, currCol);

                        // If it wasn't a penguin, plug the hole
                        if (!(obj instanceof Penguin)) {
                            hole.setPlugged(true);
                            System.out.println("The hole is now PLUGGED.");
                        }
                        sliding = false;
                        return;
                    } else {
                        // Hole is plugged, treat as empty space (pass through)
                        moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                    }
                }

                // 4b. Hitting a Movable Hazard (LightIceBlock or SeaLion)
                else if (nextObj instanceof IHazard && ((IHazard) nextObj).isMovable()) {

                    if (nextObj instanceof SeaLion) {
                        // SeaLion Logic: It moves forward, Penguin bounces BACK
                        System.out.println("BOING! SeaLion slides away, Penguin bounces back!");

                        // 1. Move the SeaLion in the SAME direction
                        // recursive call to make the SeaLion slide
                        simulateSlide(nextObj, dir);

                        // 2. Bounce the Penguin (Reverse Direction)
                        if (obj instanceof Penguin) {
                            Direction reverseDir = getOppositeDirection(dir);
                            simulateSlide(obj, reverseDir);
                        }
                    }
                    else if (nextObj instanceof LightIceBlock) {
                        // LightIceBlock Logic: It starts sliding, Penguin stops
                        System.out.println("LightIceBlock starts sliding!");
                        simulateSlide(nextObj, dir);
                        // Penguin stops here
                    }
                }

                // 4c. Hitting a Wall (HeavyIceBlock or Stopped Penguin)
                else {
                    // Just stop
                }

                sliding = false; // Collision always stops the current slide loop
            }
        }
    }


    public void simulateSlide(ITerrainObject obj, Direction dir, int maxSteps) {
        int dRow = 0, dCol = 0;
        switch (dir) {
            case UP:    dRow = -1; break;
            case DOWN:  dRow = 1; break;
            case LEFT:  dCol = -1; break;
            case RIGHT: dCol = 1; break;
        }

        boolean sliding = true;
        int stepsTaken = 0; // COUNTER

        while (sliding && stepsTaken < maxSteps) { // Check Limit
            int[] pos = getPosition(obj);
            if (pos == null) break;

            int currRow = pos[0];
            int currCol = pos[1];
            int nextRow = currRow + dRow;
            int nextCol = currCol + dCol;

            if (isOutOfBounds(nextRow, nextCol)) {
                System.out.println(obj.getSymbol() + " fell into the water!");
                handleElimination(obj);
                clearCell(currRow, currCol);
                return;
            }

            ITerrainObject nextObj = getObjectAt(nextRow, nextCol);

            // EMPTY -> Move and Increment Count
            if (nextObj == null) {
                moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                stepsTaken++;
            }
            // FOOD -> Stop
            else if (nextObj instanceof Food) {
                if (obj instanceof Penguin) {
                    moveObjectAtomic(currRow, currCol, nextRow, nextCol);
                    ((Penguin) obj).eatFood((Food) nextObj);
                    System.out.println(obj.getSymbol() + " collected food.");
                    return;
                } else {
                    moveObjectAtomic(currRow, currCol, nextRow, nextCol); // Hazards crush food
                }
            }
            // HAZARD -> Stop (Standard collision logic)
            else {
                // Call the standard slide logic just for the collision moment to reuse code
                // or just handle simple collision here. Let's keep it simple:
                System.out.println(obj.getSymbol() + " hit " + nextObj.getSymbol());

                // If hitting a hole
                if (nextObj instanceof HoleInIce && !((HoleInIce)nextObj).isPlugged()) {
                    System.out.println(obj.getSymbol() + " fell into the hole!");
                    handleElimination(obj);
                    clearCell(currRow, currCol);
                    return;
                }
                // If movable, push it
                if (nextObj instanceof IHazard && ((IHazard)nextObj).isMovable()) {
                    if (nextObj instanceof SeaLion) {
                        System.out.println("BOING! SeaLion slides away.");
                        simulateSlide(nextObj, dir); // Push SeaLion
                        // Penguin bounces back
                        simulateSlide(obj, getOppositeDirection(dir));
                    } else {
                        simulateSlide(nextObj, dir); // Push Block
                    }
                }
                return; // Stop sliding
            }
        }

        if (stepsTaken == maxSteps) {
            System.out.println(obj.getSymbol() + " stopped early due to Special Ability.");
        }
    }
    // Helper to handle elimination
    private void handleElimination(ITerrainObject obj) {
        if (obj instanceof Penguin) {
            ((Penguin) obj).eliminate();
        }
    }

    // Helper to get opposite direction
    private Direction getOppositeDirection(Direction dir) {
        switch (dir) {
            case UP: return Direction.DOWN;
            case DOWN: return Direction.UP;
            case LEFT: return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
            default: return Direction.UP;
        }
    }

    public ITerrainObject checkNextCell(ITerrainObject obj, Direction dir) {
        int[] pos = getPosition(obj);
        if (pos == null) return null; // Obje yoksa

        int dRow = 0, dCol = 0;
        switch (dir) {
            case UP:    dRow = -1; break;
            case DOWN:  dRow = 1; break;
            case LEFT:  dCol = -1; break;
            case RIGHT: dCol = 1; break;
        }

        int nextRow = pos[0] + dRow;
        int nextCol = pos[1] + dCol;

        if (isOutOfBounds(nextRow, nextCol)) return null; // Sınır dışı (Su)
        return getObjectAt(nextRow, nextCol);
    }

    // Bir sonraki karenin koordinatlarını ver
    public int[] getNextCoords(ITerrainObject obj, Direction dir) {
        int[] pos = getPosition(obj);
        if (pos == null) return null;

        int dRow = 0, dCol = 0;
        switch (dir) {
            case UP:    dRow = -1; break;
            case DOWN:  dRow = 1; break;
            case LEFT:  dCol = -1; break;
            case RIGHT: dCol = 1; break;
        }
        return new int[]{pos[0] + dRow, pos[1] + dCol};
    }
}