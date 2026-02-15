package com.zipgame.core;

public class Cell {
    private CellType type;
    private int number;
    // Walls: Top, Right, Bottom, Left
    private boolean[] walls = new boolean[4];
    private boolean visited;
    // To track the path sequence for drawing/undo
    private int visitOrder; 

    public Cell() {
        this.type = CellType.EMPTY;
        this.number = 0;
        this.visited = false;
        this.visitOrder = -1;
    }

    public Cell(int number) {
        this.type = CellType.NUMBER;
        this.number = number;
        this.visited = false;
        this.visitOrder = -1;
    }

    public void setWall(int direction, boolean hasWall) {
        if (direction >= 0 && direction < 4) {
            walls[direction] = hasWall;
        }
    }

    public boolean hasWall(int direction) {
        if (direction >= 0 && direction < 4) {
            return walls[direction];
        }
        return false;
    }

    public CellType getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited, int order) {
        this.visited = visited;
        this.visitOrder = order;
    }

    public int getVisitOrder() {
        return visitOrder;
    }
    
    public void reset() {
        this.visited = false;
        this.visitOrder = -1;
    }
}
