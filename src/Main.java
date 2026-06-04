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

        // 1. 初始化主選單 (將最後一個修改為 退出程式)
        JPanel mainMenu = new JPanel(new GridLayout(4, 1, 10, 10));
        String[] games = {"OOXX", "1A2B", "小恐龍", "退出程式"};
        
        for (String gameName : games) {
            JButton btn = new JButton(gameName);
            btn.addActionListener(e -> {
                // 關鍵修正：如果點擊的是退出程式，直接關閉 JVM
                if (gameName.equals("退出程式")) {
                    System.exit(0); 
                }

                // 其他遊戲則正常切換畫面
                cardLayout.show(container, gameName);
                
                // 如果切換到「小恐龍」，必須讓該面板取得焦點，鍵盤監聽才會生效
                if (gameName.equals("小恐龍")) {
                    for (Component comp : container.getComponents()) {
                        if (comp instanceof DinoGame) {
                            comp.requestFocusInWindow();
                        }
                    }
                }
            });
            mainMenu.add(btn);
        }

        // 2. 加入各個遊戲面板到容器中
        container.add(mainMenu, "Menu");
        
        // 注意：請確保你的 OOXX, Game1A2B 類別建構子都有接收 (Main parent)
        container.add(new OOXX(this), "OOXX");
        container.add(new Game1A2B(this), "1A2B");
        container.add(new DinoGame(this), "小恐龍");

        add(container);
        setVisible(true);
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