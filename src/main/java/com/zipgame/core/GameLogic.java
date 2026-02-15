package com.zipgame.core;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private Grid grid;
    private List<Cell> currentPath;
    private int maxNumber;
    private int startRow, startCol;

    public GameLogic(Grid grid, int maxNumber, int startRow, int startCol) {
        this.grid = grid;
        this.maxNumber = maxNumber;
        this.startRow = startRow;
        this.startCol = startCol;
        this.currentPath = new ArrayList<>();
        reset();
    }

    public void reset() {
        grid.reset();
        currentPath.clear();
        // Start path at number 1
        Cell startCell = grid.getCell(startRow, startCol);
        if (startCell != null && startCell.getNumber() == 1) {
            addToPath(startRow, startCol);
        }
    }

    public boolean tryMove(int row, int col) {
        if (!grid.isValid(row, col)) return false;

        Cell target = grid.getCell(row, col);
        
        // Check if we are backtracking
        if (currentPath.size() > 1) {
            Cell last = currentPath.get(currentPath.size() - 1);
            Cell prev = currentPath.get(currentPath.size() - 2);
            if (target == prev) {
                // Backtrack: remove last
                last.setVisited(false, -1);
                currentPath.remove(currentPath.size() - 1);
                return true;
            }
        }

        // Standard move rules
        if (target.isVisited()) return false; // Already visited (and not backtracking)

        Cell current = currentPath.get(currentPath.size() - 1);
        
        // Check adjacency and walls
        int dRow = row - getRow(current);
        int dCol = col - getCol(current);
        
        if (Math.abs(dRow) + Math.abs(dCol) != 1) return false; // Not adjacent (diagonal or jump)

        // Check walls between current and target
        int direction = -1;
        if (dRow == -1) direction = 0; // Top
        else if (dCol == 1) direction = 1; // Right
        else if (dRow == 1) direction = 2; // Bottom
        else if (dCol == -1) direction = 3; // Left

        if (current.hasWall(direction)) return false;

        // Check number sequence constraint
        if (target.getType() == CellType.NUMBER) {
            int lastNumber = getLastNumberedCell();
             // We can only visit the *next* number in sequence.
             // Actually, the rule is: connect 1, 2, 3...
             // So if we hit a number, it MUST be the next one expected.
             // Wait, if 1..path..2, all cells between 1 and 2 are effectively "1.x".
             // So if we hit a number N, the previous number visited *must* be N-1.
             if (target.getNumber() != lastNumber + 1) {
                 return false;
             }
        }

        addToPath(row, col);
        return true;
    }
    
    // Helper to find coordinates of a cell object (inefficient but safe for small grids)
    private int getRow(Cell c) {
        for(int i=0; i<grid.getRows(); i++) {
            for(int j=0; j<grid.getCols(); j++) {
                if(grid.getCell(i,j) == c) return i;
            }
        }
        return -1;
    }
    
    private int getCol(Cell c) {
        for(int i=0; i<grid.getRows(); i++) {
            for(int j=0; j<grid.getCols(); j++) {
                if(grid.getCell(i,j) == c) return j;
            }
        }
        return -1;
    }

    private void addToPath(int row, int col) {
        Cell cell = grid.getCell(row, col);
        cell.setVisited(true, currentPath.size());
        currentPath.add(cell);
    }

    private int getLastNumberedCell() {
        for (int i = currentPath.size() - 2; i >= 0; i--) {
            if (currentPath.get(i).getType() == CellType.NUMBER) {
                return currentPath.get(i).getNumber();
            }
        }
        // Base case: if no previous number found in path (shouldn't happen if we start at 1), return 0? 
        // But we start at 1. So if we are looking for the "last seen number", 
        // the very first cell is 1. If we are moving from 1, the last number is 1.
        Cell start = currentPath.get(0);
        return start.getNumber(); 
    }

    public boolean checkWin() {
        // 1. All cells visited?
        if (currentPath.size() != grid.getRows() * grid.getCols()) return false;
        
        // 2. Last cell is the max number?
        Cell last = currentPath.get(currentPath.size() - 1);
        if (last.getNumber() != maxNumber) return false;
        
        return true;
    }
    
    public Grid getGrid() {
        return grid;
    }
}
