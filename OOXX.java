import java.awt.*;
import javax.swing.*;

public class OOXX extends JPanel {
    private JButton[] buttons = new JButton[9];
    private boolean xTurn = true;

    public OOXX(Main parent) {
        setLayout(new BorderLayout());
        JPanel grid = new JPanel(new GridLayout(3, 3));
        
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
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
        backBtn.addActionListener(e -> parent.backToMenu());
        
        add(grid, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }

    private void checkWinner() {
        // 這裡寫入判斷連線的邏輯...
    }
}