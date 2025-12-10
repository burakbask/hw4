package game;

import interfaces.ITerrainObject;
import objects.*;
import enums.Direction;
import interfaces.IHazard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameManager {

    private IcyTerrain terrain;
    private List<Penguin> penguins;
    private Penguin playerPenguin; // P2 (Oyuncu)
    private final int MAX_TURNS = 4;
    private final int ROW_COUNT = 10;
    private final int COL_COUNT = 10;

    public GameManager() {
        this.terrain = new IcyTerrain();
        this.penguins = new ArrayList<>();
    }

    public void setupGame() {
        System.out.println("Welcome to Sliding Penguins Puzzle Game App.");
        System.out.println("An " + ROW_COUNT + "x" + COL_COUNT + " icy terrain grid is being generated.");
        System.out.println("Penguins, Hazards, and Food items are also being generated.");

        generatePenguins();
        terrain.generatePenguins(this.penguins);
        terrain.generateHazards();
        terrain.generateFood();
    }

    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        Random rng = new Random();

        // 1. Oyun başlamadan önceki ilk durum
        printGameState();

        for (int turn = 1; turn <= MAX_TURNS; turn++) {

            // P1 -> P2 -> P3 Sırası
            for (Penguin p : penguins) {
                if (!p.isActive()) continue; // Elenen penguenleri atla

                String answer;
                // --- BAŞLIK FORMATI ---
                String role = (p == playerPenguin) ? " (Your Penguin)" : "";
                System.out.print("*** Turn " + turn + " - " + p.getSymbol() + role);
                System.out.println(":\n");

                // --- YAPAY ZEKA (AI) TURU ---
                if (p != playerPenguin) {

                    // 1. Özel Yetenek Kullanımı (RNG)
                    boolean useAbility = false;
                    // %30 Şansla yetenek kullanımı
                    if (rng.nextInt(100) < 30) {
                        useAbility = true;
                    }

                    if(useAbility) {
                        System.out.println(p.getSymbol() + " chooses to USE its special action.");
                    } else {
                        System.out.println(p.getSymbol() + " does NOT use its special action.");
                    }

                    // 2. Yön Belirleme (AI Tarama Mantığı)
                    Direction[] directions = Direction.values();

                    // Yönleri karıştır (Sürekli aynı yöne gitmesin diye)
                    for(int i = 0; i < directions.length; i++) {
                        int index = rng.nextInt(directions.length);
                        Direction temp = directions[index];
                        directions[index] = directions[i];
                        directions[i] = temp;
                    }

                    ArrayList<Direction> directionsWithFood = new ArrayList<>();
                    ArrayList<Direction> directionsWithHazard = new ArrayList<>();

                    int[] pos = terrain.getPosition(p);
                    // Eğer penguen elendiyse veya haritada yoksa hata vermesin
                    if (pos == null) continue;

                    int pRow = pos[0];
                    int pCol = pos[1];

                    for(Direction d : directions) {
                        switch (d) {
                            case UP:
                                for (int r = pRow - 1; r >= 0; r--) {
                                    ITerrainObject o = terrain.getObjectAt(r, pCol);
                                    if (o == null) continue;
                                    if (o instanceof Food) { directionsWithFood.add(d); break; }
                                    else if (o instanceof IHazard || o instanceof Penguin) { directionsWithHazard.add(d); break; }
                                }
                                break;
                            case DOWN:
                                for (int r = pRow + 1; r < ROW_COUNT; r++) {
                                    ITerrainObject o = terrain.getObjectAt(r, pCol);
                                    if (o == null) continue;
                                    if (o instanceof Food) { directionsWithFood.add(d); break; }
                                    else if (o instanceof IHazard || o instanceof Penguin) { directionsWithHazard.add(d); break; }
                                }
                                break;
                            case LEFT:
                                for (int c = pCol - 1; c >= 0; c--) {
                                    ITerrainObject o = terrain.getObjectAt(pRow, c);
                                    if (o == null) continue;
                                    if (o instanceof Food) { directionsWithFood.add(d); break; }
                                    else if (o instanceof IHazard || o instanceof Penguin) { directionsWithHazard.add(d); break; }
                                }
                                break;
                            case RIGHT:
                                for (int c = pCol + 1; c < COL_COUNT; c++) {
                                    ITerrainObject o = terrain.getObjectAt(pRow, c);
                                    if (o == null) continue;
                                    if (o instanceof Food) { directionsWithFood.add(d); break; }
                                    else if (o instanceof IHazard || o instanceof Penguin) { directionsWithHazard.add(d); break; }
                                }
                                break;
                        }
                    }

                    // Öncelik: Yemek > Tehlike (Durmak için) > Rastgele
                    Direction bestDir = directions[0];

                    if (!directionsWithFood.isEmpty()) {
                        bestDir = directionsWithFood.get(0);
                    } else if (!directionsWithHazard.isEmpty()) {
                        bestDir = directionsWithHazard.get(0);
                    }

                    System.out.println(p.getSymbol() + " chooses to move " + bestDir);

                    // Hamleyi Gerçekleştir
                    if (useAbility) {
                        performSpecialAction(p, bestDir);
                    } else {
                        terrain.simulateSlide(p, bestDir);
                    }

                }
                // --- OYUNCU TURU ---
                else {

                    // Özel Yetenek Sorusu
                    do {
                        System.out.println("Will " + p.getSymbol() + " use its special action? Answer with Y or N --> ");
                        answer = scanner.next().trim().toUpperCase();
                    } while(!(answer.equals("Y") || answer.equals("N")));

                    boolean useAbility = answer.equals("Y");
                    if (!useAbility) {
                        System.out.println("Special action won't be used this turn.");
                    }

                    // Yön Sorusu
                    String dirInput;
                    do {
                        System.out.println("Which direction will " + p.getSymbol() + " move? Answer with U (Up), D (Down), L (Left), R (Right) --> ");
                        dirInput = scanner.next().trim().toUpperCase();
                    } while(!(dirInput.equals("U") || dirInput.equals("D") || dirInput.equals("L") || dirInput.equals("R")));

                    Direction dir = null;
                    switch(dirInput) {
                        case "U": dir = Direction.UP; break;
                        case "D": dir = Direction.DOWN; break;
                        case "L": dir = Direction.LEFT; break;
                        case "R": dir = Direction.RIGHT; break;
                    }

                    // Hamleyi Gerçekleştir
                    if (useAbility) {
                        performSpecialAction(p, dir);
                    } else {
                        terrain.simulateSlide(p, dir);
                    }
                }

                // --- GRID ÇIKTISI ---
                System.out.println("New state of the grid:");
                System.out.println();
                terrain.printTerrain();
                System.out.println();
            }
        }

        System.out.println("***** GAME OVER *****");
        printScoreboard();
    }

    // Başlangıç Durumu
    public void printGameState() {
        System.out.println("The initial icy terrain grid:");
        terrain.printTerrain();

        System.out.println("These are the penguins on the icy terrain:");
        int count = 1;
        for (Penguin p : penguins) {
            String role = "";
            if (p == playerPenguin) role = " ---> YOUR PENGUIN";

            String className = p.getClass().getSimpleName();
            String formattedName = className.replaceAll("(?<=\\p{Ll})(?=\\p{Lu})", " ");

            System.out.println("- Penguin " + count + " (" + p.getSymbol() + "): "
                    + formattedName + role);
            count++;
        }
        System.out.println();
    }

    // --- ÖZEL YETENEK MANTIĞI ---
    private void performSpecialAction(Penguin p, Direction dir) {

        if (p instanceof RoyalPenguin) {
            System.out.println(p.getSymbol() + " moves 1 square (Royal Walk).");
            simulateLimitedSlide(p, dir, 1);
        }
        else if (p instanceof KingPenguin) {
            System.out.println(p.getSymbol() + " uses King Ability (Stop at 5).");
            simulateLimitedSlide(p, dir, 5);
        }
        else if (p instanceof EmperorPenguin) {
            System.out.println(p.getSymbol() + " uses Emperor Ability (Stop at 3).");
            simulateLimitedSlide(p, dir, 3);
        }
        else if (p instanceof RockhopperPenguin) {
            System.out.println(p.getSymbol() + " prepares to jump over hazard.");

            int dRow = 0, dCol = 0;
            switch(dir){
                case UP: dRow = -1; break;
                case DOWN: dRow = 1; break;
                case LEFT: dCol = -1; break;
                case RIGHT: dCol = 1; break;
            }

            int[] currentPos = terrain.getPosition(p);
            if (currentPos == null) return;

            int startR = currentPos[0];
            int startC = currentPos[1];

            // 1. Rotayı tara: Engel bulana kadar ilerle
            ITerrainObject obstacle = null;
            int foundR = -1, foundC = -1;

            // Grid boyutu kadar (max 15 diyelim) tara
            for (int dist = 1; dist < 15; dist++) {
                int checkR = startR + (dRow * dist);
                int checkC = startC + (dCol * dist);

                if (terrain.isOutOfBounds(checkR, checkC)) break;

                ITerrainObject obj = terrain.getObjectAt(checkR, checkC);

                if (obj instanceof IHazard || obj instanceof Penguin) {
                    obstacle = obj;
                    foundR = checkR;
                    foundC = checkC;
                    break;
                }
                else if (obj instanceof Food) {
                    // Yemek varsa zıplamayı iptal et, çünkü yemekte zaten duracak.
                    break;
                }
            }

            // 2. Eğer atlanacak bir engel bulunduysa
            if (obstacle != null) {
                int landR = foundR + dRow;
                int landC = foundC + dCol;

                if(terrain.isOutOfBounds(landR, landC)) {
                    System.out.println(p.getSymbol() + " jumps over " + obstacle.getSymbol() + " but lands in water!");
                    p.eliminate();
                    terrain.clearCell(startR, startC);
                }
                else {
                    ITerrainObject landingObj = terrain.getObjectAt(landR, landC);

                    if (landingObj == null || landingObj instanceof Food) {
                        System.out.println(p.getSymbol() + " jumps over " + obstacle.getSymbol());
                        terrain.moveObjectAtomic(startR, startC, landR, landC);
                        if(landingObj instanceof Food) ((Penguin)p).eatFood((Food)landingObj);
                    } else {
                        System.out.println("Jump fail! Landing spot occupied by " + landingObj.getSymbol());
                        terrain.simulateSlide(p, dir);
                    }
                }
            } else {
                System.out.println("Nothing to jump over. Normal slide.");
                terrain.simulateSlide(p, dir);
            }
        }
    }

    // --- YENİ: ADIM ADIM İLERLEME (FREN YETENEĞİ İÇİN) ---
    private void simulateLimitedSlide(Penguin p, Direction dir, int maxSteps) {
        int dRow = 0, dCol = 0;
        switch(dir){
            case UP: dRow = -1; break;
            case DOWN: dRow = 1; break;
            case LEFT: dCol = -1; break;
            case RIGHT: dCol = 1; break;
        }

        for (int i = 0; i < maxSteps; i++) {
            int[] currentPos = terrain.getPosition(p);
            if (currentPos == null || !p.isActive()) break;

            int r = currentPos[0];
            int c = currentPos[1];

            int nextR = r + dRow;
            int nextC = c + dCol;

            // 1. Duvar/Su kontrolü
            if (terrain.isOutOfBounds(nextR, nextC)) {
                System.out.println("Hit the wall/edge.");
                break;
            }

            ITerrainObject nextObj = terrain.getObjectAt(nextR, nextC);

            // 2. Boşsa İlerle
            if (nextObj == null) {
                terrain.moveObjectAtomic(r, c, nextR, nextC);
            }
            // 3. Yemek varsa İlerle, Ye ve Dur
            else if (nextObj instanceof Food) {
                terrain.moveObjectAtomic(r, c, nextR, nextC);
                p.eatFood((Food)nextObj);
                System.out.println(p.getSymbol() + " ate " + nextObj.getSymbol() + " and stopped.");
                break;
            }
            // 4. Tehlike varsa Çarp ve Öl
            else if (nextObj instanceof IHazard) {
                System.out.println(p.getSymbol() + " hit a hazard (" + nextObj.getSymbol() + ")!");
                terrain.moveObjectAtomic(r, c, nextR, nextC); // Önce git
                p.eliminate();
                terrain.clearCell(nextR, nextC);
                break;
            }
            // 5. Başka engel varsa Çarp ve Dur
            else {
                System.out.println("Blocked by " + nextObj.getSymbol());
                break;
            }
        }
    }

    private void generatePenguins() {
        Random rand = new Random();
        for (int i = 1; i <= 3; i++) {
            String name = "P" + i;
            int type = rand.nextInt(4);
            Penguin p = null;
            switch(type) {
                case 0: p = new KingPenguin(name); break;
                case 1: p = new EmperorPenguin(name); break;
                case 2: p = new RoyalPenguin(name); break;
                case 3: p = new RockhopperPenguin(name); break;
            }
            penguins.add(p);
        }
        this.playerPenguin = penguins.get(1);
    }

    private void printScoreboard() {
        System.out.println("\n***** SCOREBOARD FOR THE PENGUINS *****");

        Collections.sort(penguins, new Comparator<Penguin>() {
            @Override
            public int compare(Penguin p1, Penguin p2) {
                return Integer.compare(p2.getTotalWeight(), p1.getTotalWeight());
            }
        });

        int rank = 1;
        for (Penguin p : penguins) {
            String role = (p == playerPenguin) ? " (Your Penguin)" : "";
            String suffix = (rank == 1) ? "st" : (rank == 2) ? "nd" : (rank == 3) ? "rd" : "th";
            System.out.println("* " + rank + suffix + " place: " + p.getSymbol() + role);
            System.out.println("|---> Total weight: " + p.getTotalWeight() + " units");
            rank++;
        }
    }
}