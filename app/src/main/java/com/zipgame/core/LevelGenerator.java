package com.zipgame.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LevelGenerator {

    private static final Random random = new Random();

    public static GameLevel generate(int rows, int cols) {
        // Try to find a hamiltonian path.
        // If it fails (rare for small grids, but possible to get stuck), retry.
        List<int[]> path = null;
        while (path == null) {
            path = findHamiltonianPath(rows, cols);
        }

        int[] start = path.get(0);
        int[] end = path.get(path.size() - 1);

        // Calculate the range of numbers to use.
        // Numbers will be 1, 2, 3... based on checkpoints.
        int step = Math.max(5, path.size() / 4);
        int checkpointCount = 1; // Start is 1

        // Count intermediates
        for (int i = step; i < path.size() - 1; i += step) {
            checkpointCount++;
        }

        // End is last
        checkpointCount++;
        int maxNumber = checkpointCount;

        GameLevel level = new GameLevel(rows, cols, start[0], start[1], maxNumber);

        // Add intermediate numbers (2, 3...)
        int currentVal = 2;
        for (int i = step; i < path.size() - 1; i += step) {
            int[] p = path.get(i);
            level.addNumber(p[0], p[1], currentVal);
            currentVal++;
        }

        // Add end number
        level.addNumber(end[0], end[1], maxNumber);

        // Add Walls
        // Basic strategy: Identifying all "non-path" edges and randomly placing walls.
        // Neighbor directions: 0:Top, 1:Right, 2:Bottom, 3:Left
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Check Right neighbor
                if (c + 1 < cols) {
                    if (!isConnected(path, r, c, r, c + 1)) {
                        if (random.nextFloat() < 0.3f) { // 30% chance of wall
                            level.addWall(r, c, 1);
                        }
                    }
                }
                // Check Bottom neighbor
                if (r + 1 < rows) {
                    if (!isConnected(path, r, c, r + 1, c)) {
                        if (random.nextFloat() < 0.3f) { // 30% chance of wall
                            level.addWall(r, c, 2);
                        }
                    }
                }
            }
        }

        return level;
    }

    private static boolean isConnected(List<int[]> path, int r1, int c1, int r2, int c2) {
        int idx1 = indexOf(path, r1, c1);
        int idx2 = indexOf(path, r2, c2);
        if (idx1 == -1 || idx2 == -1)
            return false;
        return Math.abs(idx1 - idx2) == 1;
    }

    private static int indexOf(List<int[]> path, int r, int c) {
        for (int i = 0; i < path.size(); i++) {
            int[] p = path.get(i);
            if (p[0] == r && p[1] == c)
                return i;
        }
        return -1;
    }

    private static List<int[]> findHamiltonianPath(int rows, int cols) {
        int startR = random.nextInt(rows);
        int startC = random.nextInt(cols);

        List<int[]> path = new ArrayList<>();
        boolean[][] visited = new boolean[rows][cols];

        if (dfs(rows, cols, startR, startC, visited, path)) {
            return path;
        }
        return null; // Should not happen often on empty grid
    }

    private static boolean dfs(int rows, int cols, int r, int c, boolean[][] visited, List<int[]> path) {
        visited[r][c] = true;
        path.add(new int[] { r, c });

        if (path.size() == rows * cols) {
            return true;
        }

        // Shuffle directions
        List<int[]> dirs = new ArrayList<>();
        dirs.add(new int[] { -1, 0 }); // Top
        dirs.add(new int[] { 1, 0 }); // Bottom
        dirs.add(new int[] { 0, -1 }); // Left
        dirs.add(new int[] { 0, 1 }); // Right
        Collections.shuffle(dirs, random);

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];

            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                if (dfs(rows, cols, nr, nc, visited, path)) {
                    return true;
                }
            }
        }

        // Backtrack
        visited[r][c] = false;
        path.remove(path.size() - 1);
        return false;
    }
}
