//package org.example;
//
//import java.util.Random;
//import java.util.List;
//import java.util.ArrayList;
//
//public class Labyrinth {
//    private String[][] grid;
//    private int playerX, playerY;
//    private int goalX, goalY;
//    private final int width, height;
//    private final Random random = new Random();
//
//    public Labyrinth(int width, int height) {
//        this.width = width;
//        this.height = height;
//        generateLabyrinth();
//    }
//
//    // Метод для генерации лабиринта
//    private void generateLabyrinth() {
//        grid = new String[height][width];
//        for (int i = 0; i < height; i++)
//            for (int j = 0; j < width; j++)
//                grid[i][j] = random.nextBoolean() ? "🟩" : "🟥";
//        playerX = random.nextInt(width);
//        playerY = random.nextInt(height);
//        placeGoldPixel();
//        grid[playerY][playerX] = "⬛";
//        ensureFreeCellNextToPlayer();
//    }
//
//    public void rebuildLabyrinth() {
//        String[][] newGrid = new String[height][width];
//        for (int i = 0; i < height; i++)
//            for (int j = 0; j < width; j++)
//                if (i == playerY && j == playerX)
//                    newGrid[i][j] = "⬛";
//                else if (i == goalY && j == goalX)
//                    newGrid[i][j] = "🟨";
//                else
//                    newGrid[i][j] = random.nextBoolean() ? "🟩" : "🟥";
//        grid = newGrid;
//        ensureFreeCellNextToPlayer();
//    }
//
//    private void placeGoldPixel() {
//        do {
//            goalX = random.nextInt(width);
//            goalY = random.nextInt(height);
//        } while (grid[goalY][goalX].equals("🟥") || (goalX == playerX && goalY == playerY));
//        grid[goalY][goalX] = "🟨";
//    }
//
//    public boolean movePlayer(char direction) {
//        int newX = playerX, newY = playerY;
//        switch (direction) {
//            case 'N': newY--; break;
//            case 'S': newY++; break;
//            case 'W': newX--; break;
//            case 'E': newX++; break;
//        }
//
//        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
//            if (grid[newY][newX].equals("🟨")) {
//                grid[playerY][playerX] = "🟩";
//                playerX = newX;
//                playerY = newY;
//                grid[playerY][playerX] = "⬛";
//                return false;
//            }
//            if (grid[newY][newX].equals("🟩")) {
//                grid[playerY][playerX] = "🟩";
//                playerX = newX;
//                playerY = newY;
//                grid[playerY][playerX] = "⬛";
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void ensureFreeCellNextToPlayer() {
//        List<int[]> directions = new ArrayList<>();
//        if (playerX > 0) directions.add(new int[]{playerX - 1, playerY});
//        if (playerX < width - 1) directions.add(new int[]{playerX + 1, playerY});
//        if (playerY > 0) directions.add(new int[]{playerX, playerY - 1});
//        if (playerY < height - 1) directions.add(new int[]{playerX, playerY + 1});
//
//        boolean hasFreeCell = false;
//        for (int[] dir : directions)
//            if (grid[dir[1]][dir[0]].equals("🟩")) {
//                hasFreeCell = true;
//                break;
//            }
//        if (!hasFreeCell) {
//            if (playerX > 0) grid[playerY][playerX - 1] = "🟩";
//            if (playerX < width - 1) grid[playerY][playerX + 1] = "🟩";
//            if (playerY > 0) grid[playerY - 1][playerX] = "🟩";
//            if (playerY < height - 1) grid[playerY + 1][playerX] = "🟩";
//        }
//    }
//
//    public boolean isGameOver() {
//        return playerX == goalX && playerY == goalY;
//    }
//
//    public String displayLabyrinth() {
//        StringBuilder builder = new StringBuilder();
//        for (String[] row : grid) {
//            for (String cell : row)
//                builder.append(cell);
//            builder.append("\n");
//        }
//        return builder.toString();
//    }
//}
