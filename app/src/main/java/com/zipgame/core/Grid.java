package com.zipgame.core;

public class Grid {
    private final int rows;
    private final int cols;
    private final Cell[][] cells;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (isValid(row, col)) {
            return cells[row][col];
        }
        return null;
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    // Helper to set up a level (for testing)
    public void setNumber(int row, int col, int number) {
        if (isValid(row, col)) {
            cells[row][col] = new Cell(number);
        }
    }

    // 0: Top, 1: Right, 2: Bottom, 3: Left
    public void setWall(int row, int col, int direction, boolean hasWall) {
        if (isValid(row, col)) {
            cells[row][col].setWall(direction, hasWall);
            
            // Set corresponding wall on neighbor
            int nRow = row;
            int nCol = col;
            int nDir = -1;
            
            switch (direction) {
                case 0: nRow--; nDir = 2; break; // Top -> Bottom of neighbor
                case 1: nCol++; nDir = 3; break; // Right -> Left of neighbor
                case 2: nRow++; nDir = 0; break; // Bottom -> Top of neighbor
                case 3: nCol--; nDir = 1; break; // Left -> Right of neighbor
            }
            
            if (isValid(nRow, nCol)) {
                cells[nRow][nCol].setWall(nDir, hasWall);
            }
        }
    }
    
    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j].reset();
            }
        }
    }
}
