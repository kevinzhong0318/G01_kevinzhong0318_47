import javax.swing.*;

public class Game1A2B extends JPanel {
    private Main parent; // 用來存儲主視窗引用，以便之後跳回主選單

    public Game1A2B(Main parent) {
        this.parent = parent;
        
        // 測試用：加一個標籤
        add(new JLabel("這是 1A2B 遊戲畫面"));
        
        JButton backBtn = new JButton("返回選單");
        backBtn.addActionListener(e -> parent.backToMenu());
        add(backBtn);
    }
}