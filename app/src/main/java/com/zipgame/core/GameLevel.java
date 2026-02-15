package com.zipgame.core;

import java.util.ArrayList;
import java.util.List;

public class GameLevel {
    private int rows;
    private int cols;
    private int startRow;
    private int startCol;
    private int maxNumber;

    // Simple representation for initialization
    // Number: r,c,val
    // Wall: r,c,dir (0:top, 1:right, 2:bottom, 3:left)
    private List<int[]> numbers = new ArrayList<>();
    private List<int[]> walls = new ArrayList<>();

    public GameLevel(int rows, int cols, int startRow, int startCol, int maxNumber) {
        this.rows = rows;
        this.cols = cols;
        this.startRow = startRow;
        this.startCol = startCol;
        this.maxNumber = maxNumber;
    }

    public void addNumber(int row, int col, int val) {
        numbers.add(new int[] { row, col, val });
    }

    public void addWall(int row, int col, int direction) {
        walls.add(new int[] { row, col, direction });
    }

    public Grid createGrid() {
        Grid grid = new Grid(rows, cols);

        // Add start number 1
        grid.setNumber(startRow, startCol, 1);

        for (int[] num : numbers) {
            grid.setNumber(num[0], num[1], num[2]);
        }

        for (int[] wall : walls) {
            grid.setWall(wall[0], wall[1], wall[2], true);
        }

        return grid;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }
}
