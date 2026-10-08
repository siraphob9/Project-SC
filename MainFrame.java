import java.awt.*;
import javax.swing.*;

// Owner: คนที่ 3 (Menu & Game Logic)
public class MainFrame extends JFrame {

    private User user;
    private String playerName;
    private Difficulty difficulty;

    public MainFrame(User user) {
        this.user = user;
        // ป้องกัน Error หากไม่มี User (เช่นตอนรันทดสอบ)
        this.playerName = (user != null && user.getUsername() != null) ? user.getUsername() : "Player";

        setTitle("PhotoHunt - Select Difficulty");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // จัดให้อยู่กลางจอ
        
        // ใช้พื้นหลังสีขาวเพื่อให้ปุ่มกลมกลืน
        getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE); 
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // หัวข้อหลัก
        JLabel title = new JLabel("Select Difficulty");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // หัวข้อรอง
        JLabel subtitle = new JLabel("choose your challenge");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // สร้างปุ่ม โดยใช้ฟังก์ชันสร้างปุ่มแบบแยกซ้าย-ขวา
        JButton easyBtn = createDifficultyButton("Easy");
        JButton normalBtn = createDifficultyButton("Normal");
        JButton hardBtn = createDifficultyButton("Hard");

        // กำหนดการทำงานของปุ่ม
        easyBtn.addActionListener(e -> selectDifficultyAndStart(Difficulty.EASY));
        normalBtn.addActionListener(e -> selectDifficultyAndStart(Difficulty.NORMAL));
        hardBtn.addActionListener(e -> selectDifficultyAndStart(Difficulty.HARD));

        // จัดเรียงลงในหน้าจอ (เว้นระยะด้วย createVerticalStrut)
        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(40));
        
        panel.add(easyBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(normalBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(hardBtn);

        add(panel);
    }

    // ฟังก์ชันสร้างปุ่มให้ข้อความอยู่ซ้าย ลูกศรอยู่ขวา
    private JButton createDifficultyButton(String text) {
        JButton btn = new JButton(); 
        btn.setLayout(new BorderLayout()); 
        
        // ข้อความชิดซ้าย
        JLabel textLabel = new JLabel("   " + text); 
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // เครื่องหมาย > ชิดขวา
        JLabel arrowLabel = new JLabel(">   "); 
        arrowLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        btn.add(textLabel, BorderLayout.WEST); 
        btn.add(arrowLabel, BorderLayout.EAST); 

        // ตกแต่งปุ่ม
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false); 
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // กำหนดขนาดให้เท่ากันทุกปุ่ม
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setMaximumSize(new Dimension(300, 45)); 
        
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        return btn;
    }

    private void selectDifficultyAndStart(Difficulty d) {
        this.difficulty = d;
        startGame();
    }

    public void startGame() {
        this.dispose();
        GameSession session = new GameSession(user, difficulty);
        System.out.println("====== START GAME SESSION ======");
        System.out.println("Difficulty Selected: " + difficulty.name());
        new GameFrame(session).setVisible(true);
    }
}