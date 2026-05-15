import javax.swing.*;
import java.awt.*;

public class OOXX extends JPanel {
    private JButton[] buttons = new JButton[9];
    private boolean xTurn = true;

    public OOXX(Main parent) {
        setLayout(new BorderLayout());
        JPanel grid = new JPanel(new GridLayout(3, 3));
        
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 60));
            int index = i;
            buttons[i].addActionListener(e -> {
                if (buttons[index].getText().equals("")) {
                    buttons[index].setText(xTurn ? "X" : "O");
                    xTurn = !xTurn;
                    checkWinner();
                }
            });
            grid.add(buttons[index]);
        }

        JButton backBtn = new JButton("返回主選單");
        backBtn.addActionListener(e -> {
            resetGame(); // 返回前先重置遊戲
            parent.backToMenu();
        });
        
        add(grid, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }

    private void checkWinner() {
        // 定義所有連線成功的索引組合 (橫、豎、斜)
        int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // 橫
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // 豎
            {0, 4, 8}, {2, 4, 6}             // 斜
        };

        for (int[] condition : winConditions) {
            String b1 = buttons[condition[0]].getText();
            String b2 = buttons[condition[1]].getText();
            String b3 = buttons[condition[2]].getText();

            if (!b1.equals("") && b1.equals(b2) && b2.equals(b3)) {
                JOptionPane.showMessageDialog(this, "恭喜 " + b1 + " 獲勝！");
                resetGame();
                return;
            }
        }

        // 檢查是否平局 (所有格子都滿了)
        boolean full = true;
        for (JButton btn : buttons) {
            if (btn.getText().equals("")) {
                full = false;
                break;
            }
        }

        if (full) {
            JOptionPane.showMessageDialog(this, "平局！");
            resetGame();
        }
    }

    private void resetGame() {
        for (JButton btn : buttons) {
            btn.setText("");
        }
        xTurn = true;
    }
}