import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class DinoGame extends JPanel implements ActionListener, KeyListener {
    private Main parent; // 持有 Main 的引用以便返回選單
    private final int WIDTH = 600; // 配合你的 Main 視窗大小
    private final int HEIGHT = 600;
    
    private int dinoY = 400, dinoX = 50, dinoSize = 40;
    private int velocityY = 0;
    private final int GRAVITY = 2;
    private ArrayList<Rectangle> obstacles;
    private Timer timer;
    private int score = 0;
    private boolean isGameOver = false;

    // 修改建構子，接收 Main 實例
    public DinoGame(Main parent) {
        this.parent = parent;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        obstacles = new ArrayList<>();
        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 畫地面
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, 440, WIDTH, 440);

        // 畫恐龍
        g.setColor(Color.DARK_GRAY);
        g.fillRect(dinoX, dinoY, dinoSize, dinoSize);

        // 畫障礙物
        g.setColor(Color.RED);
        for (Rectangle rect : obstacles) {
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // 畫分數
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 480, 30);

        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.drawString("GAME OVER!", 230, 250);
            g.drawString("Press 'R' to Restart", 210, 280);
            g.drawString("Press 'ESC' to Menu", 210, 310);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver) return;

        dinoY += velocityY;
        if (dinoY < 400) {
            velocityY += GRAVITY;
        } else {
            dinoY = 400;
            velocityY = 0;
        }

        if (new Random().nextInt(100) < 2) {
            obstacles.add(new Rectangle(WIDTH, 410, 20, 30));
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle rect = obstacles.get(i);
            rect.x -= 8;

            if (rect.intersects(new Rectangle(dinoX, dinoY, dinoSize, dinoSize))) {
                isGameOver = true;
                timer.stop();
            }

            if (rect.x + rect.width < 0) {
                obstacles.remove(i);
                score++;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && dinoY >= 400) {
            velocityY = -22;
        }
        if (isGameOver) {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                restartGame();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                parent.backToMenu(); // 返回主選單
            }
        }
    }

    private void restartGame() {
        dinoY = 400;
        obstacles.clear();
        score = 0;
        isGameOver = false;
        timer.start();
        repaint();
    }

    // 必須請求焦點，否則按鍵沒反應
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}