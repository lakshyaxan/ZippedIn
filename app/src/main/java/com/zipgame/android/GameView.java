package com.zipgame.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zipgame.core.*;

public class GameView extends View {

    public interface OnLevelCompleteListener {
        void onLevelComplete(long completionTime);
    }

    private GameLogic gameLogic;
    private GameLevel currentLevel;
    private int CELL_SIZE = 100;

    private Paint gridPaint;
    private Paint textPaint;
    private Paint wallPaint;
    private Paint pathPaint;

    private long startTime;
    private OnLevelCompleteListener onLevelCompleteListener;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(2);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        wallPaint = new Paint();
        wallPaint.setStyle(Paint.Style.STROKE); // I AM SO, SO, SO SORRY. THIS IS THE FIX.
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(10f);
        wallPaint.setAntiAlias(true);

        pathPaint = new Paint();
        pathPaint.setColor(Color.GREEN);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setAntiAlias(true);

        startNewLevel();
    }

    public void setOnLevelCompleteListener(OnLevelCompleteListener listener) {
        this.onLevelCompleteListener = listener;
    }

    public void startNewLevel() {
        currentLevel = LevelGenerator.generate(6, 6);
        resetLevel();
    }

    public void resetLevel() {
        if (currentLevel == null) return;
        Grid grid = currentLevel.createGrid();
        gameLogic = new GameLogic(
                grid,
                currentLevel.getMaxNumber(),
                currentLevel.getStartRow(),
                currentLevel.getStartCol()
        );
        startTime = System.currentTimeMillis();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (gameLogic != null) {
            int rows = gameLogic.getGrid().getRows();
            int cols = gameLogic.getGrid().getCols();

            CELL_SIZE = Math.min(w / cols, h / rows);
            textPaint.setTextSize(CELL_SIZE / 2f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gameLogic == null) return;

        Grid grid = gameLogic.getGrid();
        int rows = grid.getRows();
        int cols = grid.getCols();

        int gridWidth = cols * CELL_SIZE;
        int gridHeight = rows * CELL_SIZE;

        int offsetX = (getWidth() - gridWidth) / 2;
        int offsetY = (getHeight() - gridHeight) / 2;

        canvas.save();
        canvas.translate(offsetX, offsetY);

        // 1. Clear the canvas
        canvas.drawColor(Color.parseColor("#eeeeee"));

        // 2. Draw cell contents (path and numbers)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;
                Cell cell = grid.getCell(r, c);

                if (cell.isVisited()) {
                    canvas.drawRect(x + 5, y + 5, x + CELL_SIZE - 5, y + CELL_SIZE - 5, pathPaint);
                }

                if (cell.getType() == CellType.NUMBER) {
                    canvas.drawText(String.valueOf(cell.getNumber()), x + CELL_SIZE / 2f, y + CELL_SIZE / 2f + textPaint.getTextSize() / 3, textPaint);
                }
            }
        }

        // 3. Draw the complete grid with thin lines
        for (int i = 0; i <= rows; i++) {
            canvas.drawLine(0, i * CELL_SIZE, gridWidth, i * CELL_SIZE, gridPaint);
        }
        for (int i = 0; i <= cols; i++) {
            canvas.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, gridHeight, gridPaint);
        }

        // 4. Draw the walls with thick lines on top
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;
                Cell cell = grid.getCell(r, c);

                if (cell.hasWall(0)) canvas.drawLine(x, y, x + CELL_SIZE, y, wallPaint);
                if (cell.hasWall(1)) canvas.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE, wallPaint);
                if (cell.hasWall(2)) canvas.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE, wallPaint);
                if (cell.hasWall(3)) canvas.drawLine(x, y, x, y + CELL_SIZE, wallPaint);
            }
        }

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameLogic == null) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN ||
            event.getAction() == MotionEvent.ACTION_MOVE) {

            Grid grid = gameLogic.getGrid();
            int rows = grid.getRows();
            int cols = grid.getCols();

            int gridWidth = cols * CELL_SIZE;
            int gridHeight = rows * CELL_SIZE;

            int offsetX = (getWidth() - gridWidth) / 2;
            int offsetY = (getHeight() - gridHeight) / 2;

            float x = event.getX() - offsetX;
            float y = event.getY() - offsetY;

            int c = (int)(x / CELL_SIZE);
            int r = (int)(y / CELL_SIZE);

            if (r >= 0 && r < rows && c >= 0 && c < cols) {
                if (gameLogic.tryMove(r, c)) {
                    invalidate();

                    if (gameLogic.checkWin()) {
                        if (onLevelCompleteListener != null) {
                            onLevelCompleteListener.onLevelComplete(
                                    System.currentTimeMillis() - startTime);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
