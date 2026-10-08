import javax.swing.*;
import java.awt.*;

// Owner: คนที่ 3 (Menu & Game Logic)
public class MainFrame extends JFrame {

    private User user;
    private String playerName;
    private Difficulty difficulty;

    public MainFrame(User user) {
        this.user = user;
        this.playerName = user.getUsername(); // สมมติ User มี getUsername()

        setTitle("PhotoHunt - Select Difficulty");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // หัวข้อ
        JLabel title = new JLabel("Welcome, " + playerName);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Select Difficulty");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // สร้างปุ่ม (สมมติใช้แบบง่ายไปก่อน)
        JButton btnEasy = new JButton("Easy (5 Spots)");
        JButton btnNormal = new JButton("Normal (7 Spots)");
        JButton btnHard = new JButton("Hard (9 Spots)");

        // จัดปุ่มให้อยู่กึ่งกลาง
        btnEasy.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNormal.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ใส่ Action ให้ปุ่ม
        btnEasy.addActionListener(e -> selectDifficultyAndStart(Difficulty.EASY));
        btnNormal.addActionListener(e -> selectDifficultyAndStart(Difficulty.NORMAL));
        btnHard.addActionListener(e -> selectDifficultyAndStart(Difficulty.HARD));

        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(40));
        panel.add(btnEasy);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnNormal);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnHard);

        add(panel);
    }

    private void selectDifficultyAndStart(Difficulty d) {
        this.difficulty = d;
        startGame();
    }

    public void startGame() {
        // ปิดหน้าต่างนี้
        this.dispose();
        
        // สร้าง GameSession โยน user และ difficulty เข้าไป
        GameSession session = new GameSession(user, difficulty);
        
        // เรียก GameFrame (หน้าของคนที่ 4) ขึ้นมาทำงาน
         new GameFrame(session).setVisible(true); // รอ GameFrame ของคนที่ 4
    }
}