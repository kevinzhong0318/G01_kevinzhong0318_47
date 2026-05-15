import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel container = new JPanel(cardLayout);

    public Main() {
        setTitle("小樂園 Game Hub");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化主選單
        JPanel mainMenu = new JPanel(new GridLayout(4, 1, 10, 10));
        String[] games = {"OOXX", "1A2B", "小恐龍", "踩地雷"};
        
        for (String game : games) {
            JButton btn = new JButton(game);
            btn.addActionListener(e -> cardLayout.show(container, game));
            mainMenu.add(btn);
        }

        // 加入各個遊戲面板 (這裡先預留介面)
        container.add(mainMenu, "Menu");
        container.add(new OOXX(this), "OOXX");
        container.add(new Game1A2B(this), "1A2B");
        // 恐龍與踩地雷依此類推...

        add(container);
        setVisible(true);
    }

    public void backToMenu() {
        cardLayout.show(container, "Menu");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}