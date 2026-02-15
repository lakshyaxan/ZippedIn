package com.zipgame.desktop;

import com.zipgame.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DesktopLauncher extends JFrame {
    private GameLogic gameLogic;
    private JPanel gamePanel;
    private final int CELL_SIZE = 60;

    public DesktopLauncher() {
        setTitle("Zip Game Prototype");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use Generator
        GameLevel level = LevelGenerator.generate(6, 6);

        Grid grid = level.createGrid();
        gameLogic = new GameLogic(grid, level.getMaxNumber(), level.getStartRow(), level.getStartCol());

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };

        gamePanel.setPreferredSize(new Dimension(grid.getCols() * CELL_SIZE, grid.getRows() * CELL_SIZE));
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleInput(e.getX(), e.getY());
            }
        });

        gamePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleInput(e.getX(), e.getY());
            }
        });

        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void handleInput(int x, int y) {
        int r = y / CELL_SIZE;
        int c = x / CELL_SIZE;

        if (gameLogic.tryMove(r, c)) {
            gamePanel.repaint();
            if (gameLogic.checkWin()) {
                JOptionPane.showMessageDialog(this, "Level Complete!");
                gameLogic.reset();
                gamePanel.repaint();
            }
        }
    }

    private void drawGame(Graphics g) {
        Grid grid = gameLogic.getGrid();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;

                Cell cell = grid.getCell(r, c);

                // Draw Cell Background
                g2.setColor(Color.WHITE);
                g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                // Draw Path
                if (cell.isVisited()) {
                    g2.setColor(new Color(100, 200, 100)); // Greenish
                    g2.fillRect(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                }

                // Draw Number
                if (cell.getType() == CellType.NUMBER) {
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 20));
                    String s = String.valueOf(cell.getNumber());
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(s);
                    int th = fm.getAscent();
                    g2.drawString(s, x + (CELL_SIZE - tw) / 2, y + (CELL_SIZE + th) / 2 - 5);
                }

                // Draw Walls (Bold lines)
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(4));
                if (cell.hasWall(0))
                    g2.drawLine(x, y, x + CELL_SIZE, y); // Top
                if (cell.hasWall(1))
                    g2.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE); // Right
                if (cell.hasWall(2))
                    g2.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE); // Bottom
                if (cell.hasWall(3))
                    g2.drawLine(x, y, x, y + CELL_SIZE); // Left
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesktopLauncher().setVisible(true));
    }
}
