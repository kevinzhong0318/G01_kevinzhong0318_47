package src;
import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel container = new JPanel(cardLayout);

    public Main() {
        setTitle("小樂園 Game Hub");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. 初始化主選單
        JPanel mainMenu = new JPanel(new GridLayout(4, 1, 10, 10));
        String[] games = {"OOXX", "1A2B", "小恐龍", "退出程式"};
        
        for (String gameName : games) {
            JButton btn = new JButton(gameName);
            btn.addActionListener(e -> {
                // 如果點擊的是退出程式，直接關閉 JVM
                if (gameName.equals("退出程式")) {
                    System.exit(0); 
                }

                if (gameName.equals("小恐龍")) {
                    // 先移除舊的（如果有的話），確保每次都是全新一局
                    removeComponentByName("小恐龍");
                    
                    
                    // 建立全新的小恐龍遊戲
                    DinoGame dinoGame = new DinoGame(this);
                    container.add(dinoGame, "小恐龍");
                    
                    // 切換畫面
                    cardLayout.show(container, "小恐龍");
                    
                    // 強制抓取鍵盤焦點，並啟動遊戲！
                    dinoGame.requestFocusInWindow();
                    dinoGame.startGame(); // 呼叫 DinoGame 的開始方法
                } else {
                    // 其他靜態遊戲正常切換畫面
                    cardLayout.show(container, gameName);
                }
            });
            mainMenu.add(btn);
        }

        // 2. 加入固定不變的遊戲面板到容器中
        container.add(mainMenu, "Menu");
        container.add(new OOXX(this), "OOXX");
        container.add(new Game1A2B(this), "1A2B");
        
        // 🛠️ 核心修正：這裡不再預先 new DinoGame(this) 了！把那一行刪除

        add(container);
        setVisible(true);
    }

    /**
     * 輔助方法：用來動態移除 container 中舊的面板
     */
    private void removeComponentByName(String name) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof DinoGame) {
                container.remove(comp);
                break;
            }
        }
    }

    /**
     * 提供給子遊戲面板呼叫，用來回到主選單
     */
    public void backToMenu() {
        cardLayout.show(container, "Menu");
    }

    public static void main(String[] args) {
        // 使用事件調度線程啟動 Swing 程式
        SwingUtilities.invokeLater(() -> {
            new Main();
        });
    }
}