import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Game1A2B extends JPanel {
    private Main parent;
    private String answer;
    private JTextField inputField;
    private JTextArea logArea;
    private JButton submitBtn;

    public Game1A2B(Main parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));

        // --- 上方：標題與輸入區 ---
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("輸入 4 位不重複數字:"));
        inputField = new JTextField(10);
        submitBtn = new JButton("送出");
        topPanel.add(inputField);
        topPanel.add(submitBtn);
        add(topPanel, BorderLayout.NORTH);

        // --- 中間：猜測紀錄 ---
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // --- 下方：功能按鈕 ---
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton restartBtn = new JButton("重新開始");
        JButton backBtn = new JButton("返回主選單");
        
        bottomPanel.add(restartBtn);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 事件監聽 ---
        submitBtn.addActionListener(e -> checkGuess());
        inputField.addActionListener(e -> checkGuess()); // 按 Enter 也能送出
        restartBtn.addActionListener(e -> initGame());
        backBtn.addActionListener(e -> parent.backToMenu());

        // 初始化遊戲
        initGame();
    }
    private void initGame() {
        // 產生不重複的 4 位數
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i <= 9; i++) numbers.add(i);
        Collections.shuffle(numbers);
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(numbers.get(i));
        }
        answer = sb.toString();
        
        logArea.setText("遊戲開始！請輸入四個不同的數字。\n");
        inputField.setText("");
        inputField.setEnabled(true);
        submitBtn.setEnabled(true);
        // System.out.println("Debug 答案: " + answer); // 除錯用
    }
    
}