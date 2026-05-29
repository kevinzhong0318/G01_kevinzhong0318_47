import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class DinoGame extends JPanel implements ActionListener, KeyListener {
    private Main parent;
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int GROUND_Y = 400; 
    
    // 恐龍屬性
    private int dinoY = GROUND_Y, dinoX = 50, dinoWidth = 40, dinoHeight = 40;
    private int velocityY = 0;
    private final int GRAVITY = 2;
    private boolean isCrouching = false;
    
    // 障礙物與動態速度
    private ArrayList<Rectangle> obstacles;
    private double gameSpeed = 8.0;         // 初始移動速度
    private final double MAX_SPEED = 25.0;  // 最高移動速度限制
    private int minDistance = 250;          // 基礎最小間距
    
    private Timer timer;
    private int score = 0; // 現在分數代表存活的幀數
    private boolean isGameOver = false;

    public DinoGame(Main parent) {
        this.parent = parent;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        obstacles = new ArrayList<>();
        timer = new Timer(20, this); // 約 50 FPS
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 分數達到 700 變黑夜
        boolean isNight = (score >= 700);
        if (isNight) {
            setBackground(new Color(30, 30, 30));
            g.setColor(Color.WHITE);
        } else {
            setBackground(Color.WHITE);
            g.setColor(Color.BLACK);
        }
        
        g.drawLine(0, GROUND_Y + 40, WIDTH, GROUND_Y + 40);

        // 畫恐龍 (蹲下時顏色稍微深一點)
        if (isNight) g.setColor(isCrouching ? Color.CYAN.darker() : Color.CYAN);
        else g.setColor(isCrouching ? Color.GRAY : Color.DARK_GRAY);
        g.fillRect(dinoX, dinoY, dinoWidth, dinoHeight);

        // 畫障礙物
        for (Rectangle rect : obstacles) {
            if (rect.height < 30) g.setColor(Color.ORANGE); // 小鳥
            else g.setColor(Color.RED); // 仙人掌
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // 顯示分數與目前速度
        g.setColor(isNight ? Color.WHITE : Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString(String.format("Score: %05d", score), 450, 30);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(String.format("Speed: %.1f", gameSpeed), 450, 50);

        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", 210, 250);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press 'R' to Restart", 215, 290);
            g.drawString("Press 'ESC' to Menu", 215, 320);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver) return;

        // 1. 分數增加 (隨時間) 與速度成長
        score++;
        if (gameSpeed < MAX_SPEED) {
            gameSpeed = 8.0 + (score / 400.0); // 每 400 幀(約8秒)速度加 1
        }

        // 2. 物理邏輯
        dinoY += velocityY;
        if (dinoY < GROUND_Y) {
            velocityY += GRAVITY;
        } else {
            dinoY = GROUND_Y;
            velocityY = 0;
            if (isCrouching) {
                dinoHeight = 20;
                dinoY = GROUND_Y + 20;
            } else {
                dinoHeight = 40;
                dinoY = GROUND_Y;
            }
        }

        // 3. 障礙物生成 (隨速度動態調整最小間距)
        int dynamicMinDist = (int)(minDistance + gameSpeed * 5); 
        boolean canSpawn = obstacles.isEmpty() || (WIDTH - obstacles.get(obstacles.size() - 1).x >= dynamicMinDist);
        
        if (canSpawn && new Random().nextInt(100) < 3) {
            // 分數越高，小鳥出現機率越高
            if (new Random().nextInt(1000) > score) {
                // 仙人掌
                obstacles.add(new Rectangle(WIDTH, GROUND_Y + 10, 20, 30));
            } else {
                // 小鳥 (必須蹲下躲避)
                obstacles.add(new Rectangle(WIDTH, GROUND_Y - 5, 30, 20));
            }
        }

        // 4. 移動與碰撞偵測
        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle rect = obstacles.get(i);
            rect.x -= (int)gameSpeed; // 使用動態速度

            if (rect.intersects(new Rectangle(dinoX, dinoY, dinoWidth, dinoHeight))) {
                isGameOver = true;
                timer.stop();
            }

            if (rect.x + rect.width < 0) {
                obstacles.remove(i);
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) && dinoY >= GROUND_Y && !isCrouching) {
            velocityY = -22;
        }
        if (code == KeyEvent.VK_DOWN) {
            isCrouching = true;
        }
        
        if (isGameOver) {
            if (code == KeyEvent.VK_R) restartGame();
            else if (code == KeyEvent.VK_ESCAPE) parent.backToMenu();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            isCrouching = false;
            if (dinoY >= GROUND_Y) {
                dinoHeight = 40;
                dinoY = GROUND_Y;
            }
        }
    }

    private void restartGame() {
        dinoY = GROUND_Y;
        dinoHeight = 40;
        gameSpeed = 8.0; // 重置速度
        obstacles.clear();
        score = 0;
        isGameOver = false;
        isCrouching = false;
        timer.start();
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override public void keyTyped(KeyEvent e) {}
}