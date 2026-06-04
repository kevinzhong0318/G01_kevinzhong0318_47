package src;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
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
    private double gameSpeed = 8.0;         
    private final double MAX_SPEED = 25.0;  
    private int minDistance = 250;          
    
    // 圖片資源
    private BufferedImage dinoImg, dinoSitImg, cactusImg, cactusClusterImg, pteroImg;

    private Timer timer;
    private int score = 0; 
    private boolean isGameOver = false;

    public DinoGame(Main parent) {
        this.parent = parent;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        try {
            // 讀取 image 資料夾下的所有專案圖片
            dinoImg = ImageIO.read(new File("image" + File.separator + "dino.png"));
            dinoSitImg = ImageIO.read(new File("image" + File.separator + "dino_sit.png"));
            cactusImg = ImageIO.read(new File("image" + File.separator + "cactus.png"));
            cactusClusterImg = ImageIO.read(new File("image" + File.separator + "cactus_cluster.png"));
            pteroImg = ImageIO.read(new File("image" + File.separator + "pterodactyl.png"));
        } catch (IOException e) {
            System.err.println("Could not load images. Check filenames and path.");
            e.printStackTrace();
            System.exit(1);
        }

        obstacles = new ArrayList<>();
        timer = null;
    }
    public void startGame() {
        // 1. 初始化所有基礎遊戲數據，確保重頭開始
        dinoY = GROUND_Y;
        dinoHeight = 40;
        gameSpeed = 8.0; 
        obstacles.clear();
        score = 0;
        isGameOver = false;
        isCrouching = false;
        
        // 2. 核心修正：安全關閉舊的計時器，並在此時才建立新計時器
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(20, this); // 約 50 FPS
        timer.start();
        
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 每 700 分切換一次白天與黑夜 (奇數關卡白天，偶數關卡黑夜)
        boolean isNight = ((score / 700) % 2 != 0);
        if (isNight) {
            setBackground(new Color(30, 30, 30)); // 暗色背景
            g.setColor(Color.WHITE);
        } else {
            setBackground(Color.WHITE); // 亮色背景
            g.setColor(Color.BLACK);
        }
        
        // 畫固定的地面基準線
        g.drawLine(0, GROUND_Y + 40, WIDTH, GROUND_Y + 40);

        // 畫角色 (根據蹲下狀態切換站立/蹲下圖片)
        if (isCrouching) {
            g.drawImage(dinoSitImg, dinoX, dinoY, dinoWidth, dinoHeight, null);
        } else {
            g.drawImage(dinoImg, dinoX, dinoY, dinoWidth, dinoHeight, null);
        }

        // 畫障礙物 (依據矩形特徵判斷要渲染哪張圖片)
        for (Rectangle rect : obstacles) {
            if (rect.height < 30) {
                // 翼手龍
                g.drawImage(pteroImg, rect.x, rect.y, rect.width, rect.height, null);
            } else if (rect.width > 25) {
                // 仙人掌叢
                g.drawImage(cactusClusterImg, rect.x, rect.y, rect.width, rect.height, null);
            } else {
                // 單顆仙人掌
                g.drawImage(cactusImg, rect.x, rect.y, rect.width, rect.height, null);
            }
        }

        // UI 資訊看板
        g.setColor(isNight ? Color.WHITE : Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString(String.format("Score: %05d", score), 430, 30);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(String.format("Speed: %.1f", gameSpeed), 430, 50);
        
        int currentStage = (score / 700) + 1;
        g.drawString("Stage: " + currentStage, 430, 70);

        // 遊戲結束覆蓋層
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

        // 1. 分數與速度隨時間平滑成長
        score++;
        if (gameSpeed < MAX_SPEED) {
            gameSpeed = 8.0 + (score / 400.0); 
        }

        // 2. 恐龍蹲下與跳躍狀態的動態 Hitbox 尺寸修正
        if (isCrouching && velocityY == 0 && dinoY >= GROUND_Y) {
            dinoHeight = 20;          // 碰撞箱高度砍半
            dinoY = GROUND_Y + 20;    // 座標下移，貼緊地面
        } else {
            dinoHeight = 40;          // 恢復正常高度
            if (velocityY == 0 && dinoY >= GROUND_Y) {
                dinoY = GROUND_Y;
            }
        }

        // 3. 物理重力模擬
        dinoY += velocityY;
        if (dinoY < (isCrouching ? GROUND_Y + 20 : GROUND_Y)) {
            velocityY += GRAVITY; // 空中受重力加速度
        } else {
            dinoY = isCrouching ? GROUND_Y + 20 : GROUND_Y;
            velocityY = 0;        // 落地
        }

        // 4. 障礙物動態生成 (70分以下為安全期，不生成任何障礙物)
        int dynamicMinDist = (int)(minDistance + gameSpeed * 5); 
        boolean canSpawn = obstacles.isEmpty() || (WIDTH - obstacles.get(obstacles.size() - 1).x >= dynamicMinDist);
        
        if (score > 70 && canSpawn && new Random().nextInt(100) < 3) {
            if (score < 700) {
                // --- 700分前：純仙人掌階段 ---
                if (new Random().nextBoolean()) {
                    obstacles.add(new Rectangle(WIDTH, GROUND_Y + 10, 20, 30)); // 單顆
                } else {
                    obstacles.add(new Rectangle(WIDTH, GROUND_Y + 10, 40, 30)); // 叢生
                }
            } else {
                // --- 700分後：進入黑夜，開啟翼手龍大亂鬥階段 ---
                if (new Random().nextBoolean()) {
                    // 出仙人掌
                    if (new Random().nextBoolean()) {
                        obstacles.add(new Rectangle(WIDTH, GROUND_Y + 10, 20, 30));
                    } else {
                        obstacles.add(new Rectangle(WIDTH, GROUND_Y + 10, 40, 30));
                    }
                } else {
                    // 出翼手龍 (隨機高空或貼地)
                    if (new Random().nextBoolean()) {
                        obstacles.add(new Rectangle(WIDTH, GROUND_Y - 10, 30, 20)); // 高空翼手龍
                    } else {
                        obstacles.add(new Rectangle(WIDTH, GROUND_Y + 20, 30, 20)); // 低空翼手龍
                    }
                }
            }
        }

        // 5. 障礙物位移與精密碰撞偵測
        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle rect = obstacles.get(i);
            rect.x -= (int)gameSpeed; 

            // --- 精密碰撞優化：將實體邊框內縮，去除圖片留白造成的誤判 ---
            int dPadX = 4;
            int dPadY = 2;
            Rectangle fineDinoHitbox = new Rectangle(
                dinoX + dPadX, 
                dinoY + dPadY, 
                dinoWidth - (dPadX * 2), 
                dinoHeight - (dPadY * 2)
            );

            // 根據不同障礙物給予不同的邊距容錯
            int oPadX = 3; 
            int oPadY = 3; 
            
            if (rect.height < 30) {
                // 翼手龍：翅膀有上下揮動留白，上下判定多縮一點
                oPadX = 4;
                oPadY = 5;
            } else if (rect.width > 25) {
                // 仙人掌叢：體積大，左右兩側多縮一點
                oPadX = 5;
                oPadY = 2;
            } else {
                // 單顆仙人掌
                oPadX = 3;
                oPadY = 2;
            }

            Rectangle fineObstacleHitbox = new Rectangle(
                rect.x + oPadX, 
                rect.y + oPadY, 
                rect.width - (oPadX * 2), 
                rect.height - (oPadY * 2)
            );

            // 使用精準化後的碰撞箱進行交叉判定
            if (fineDinoHitbox.intersects(fineObstacleHitbox)) {
                isGameOver = true;
                if (timer != null) timer.stop();
            }

            // 移出螢幕後自動釋放記憶體
            if (rect.x + rect.width < 0) {
                obstacles.remove(i);
                i--; // 核心修正：移除元素後索引減 1，避免漏算下一個物件
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // 跳躍：支援空白鍵與上方向鍵
        if ((code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) && dinoY >= GROUND_Y && !isCrouching) {
            velocityY = -17;
        }
        
        // 蹲下：按住下方向鍵
        if (code == KeyEvent.VK_DOWN && dinoY >= GROUND_Y) {
            isCrouching = true;
        }
        
        // 遊戲結束時的按鍵處理
        if (isGameOver) {
            if (code == KeyEvent.VK_R) {
                startGame(); // 重新開始直接呼叫包裝好的啟動方法
            } else if (code == KeyEvent.VK_ESCAPE) {
                if (timer != null) timer.stop(); // 回選單前確實停止計時器
                parent.backToMenu();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            isCrouching = false;
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow(); 
    }

    @Override public void keyTyped(KeyEvent e) {}
}