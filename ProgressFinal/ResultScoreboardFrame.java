import java.awt.BorderLayout; // ตัวจัดวางแบบ เหนือ/ใต้/ซ้าย/ขวา/กลาง
import java.awt.Color;        // ชนิดข้อมูลสี
import java.awt.Component;    // ชนิดแม่ของชิ้นส่วนหน้าจอ (ใช้ค่าจัดแนว CENTER_ALIGNMENT)
import java.awt.Cursor;       // เคอร์เซอร์เมาส์ (เช่น รูปมือ)
import java.awt.Dimension;    // ขนาด กว้าง x สูง
import java.awt.Font;         // ฟอนต์ตัวอักษร

import javax.swing.*;                  // ชิ้นส่วนหน้าต่าง Swing (JFrame, JPanel, JLabel, JButton, BoxLayout ...)
import javax.swing.border.EmptyBorder; // ขอบว่างเปล่า (ใช้เว้นระยะรอบๆ)

// คลาส ResultScoreboardFrame = "หน้าสรุปผลหลังจบเกม" แสดงคะแนน ตาราง Top 5 และปุ่มเล่นอีก/กลับเมนู
public class ResultScoreboardFrame extends JFrame {

    private static final int TOP_COUNT = 5; // แสดงกี่อันดับในตารางคะแนน (static final = ค่าคงที่ของทั้งคลาส)

    private GameResult result;       // ผลของเกมที่เพิ่งเล่นจบ
    private CSVManager csvManager;   // ตัวอ่านไฟล์คะแนน
    private User user;               // ผู้เล่นคนนี้ (ไว้ส่งต่อเวลากด Play again / Back to menu)
    private JPanel root;             // แผงหลักที่ใส่ทุกอย่างไว้ข้างใน

    // constructor: รับผลเกมกับผู้เล่น แล้วประกอบหน้าตาทั้งหน้า
    public ResultScoreboardFrame(GameResult result, User user) {
        super("PhotoHunt - Result"); // ตั้งชื่อบนแถบหัวหน้าต่าง
        this.result = result;        // เก็บผลเกม
        this.user = user;            // เก็บผู้เล่น
        this.csvManager = new CSVManager(); // สร้างตัวอ่านไฟล์

        root = new JPanel(); // สร้างแผงหลัก
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS)); // เรียงของในแผงจากบนลงล่าง
        root.setBackground(Color.white); // พื้นหลังสีขาว
        root.setBorder(new EmptyBorder(40, 60, 40, 60)); // เว้นขอบรอบแผง: บน 40, ซ้าย 60, ล่าง 40, ขวา 60

        JLabel title = new JLabel("Game Over", SwingConstants.CENTER); // หัวข้อ "Game Over" จัดกลาง
        title.setFont(new Font("SaneSerif", Font.BOLD, 22)); // ฟอนต์ตัวหนาขนาด 22 (ชื่อ "SaneSerif" สะกดผิด ควรเป็น SansSerif Java จะใช้ฟอนต์สำรองแทน)
        title.setForeground(new Color(0x1F1F1E)); // สีตัวอักษรเกือบดำ
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดให้อยู่กึ่งกลางแนวนอน
        root.add(Box.createVerticalStrut(90)); // ช่องว่างแนวตั้ง 90 พิกเซล (ดันเนื้อหาลงมา)
        root.add(title);                       // ใส่หัวข้อ
        root.add(Box.createVerticalStrut(5));  // เว้น 5 พิกเซล

        root.add(buildScoreCard()); // ใส่การ์ดคะแนน
        if (user != null && user.isGuest()) { // ถ้ามีผู้เล่นและเป็นแขก
            root.add(Box.createVerticalStrut(8)); // เว้น 8 พิกเซล
            JLabel guestNotice = new JLabel("* Guest Mode: Score not saved (No account)", SwingConstants.CENTER); // ข้อความเตือนว่าไม่บันทึกคะแนน
            guestNotice.setFont(new Font("SansSerif", Font.ITALIC, 12)); // ตัวเอียง ขนาด 12
            guestNotice.setForeground(new Color(0xE74C3C)); // สีแดงเตือน
            guestNotice.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
            root.add(guestNotice); // ใส่ข้อความเตือน
        }

        root.add(Box.createVerticalStrut(20)); // เว้น 20 พิกเซล
       

        root.add(buildScoreboardPanel(result.getDifficulty())); // ใส่ตาราง Top 5 ของระดับที่เพิ่งเล่น

        JButton playAgain = buildButton("Play again"); // สร้างปุ่ม "Play again"
        playAgain.addActionListener(e -> { // ก้อนคำสั่งที่ทำเมื่อกดปุ่ม
            dispose(); // ปิดหน้าสรุปผลนี้
            new GameFrame(new GameSession(user, result.getDifficulty())).setVisible(true); // เริ่มเกมใหม่ระดับเดิม (session ใหม่ + หน้าเกมใหม่)
        });
        root.add(playAgain); // ใส่ปุ่มลงแผง
        root.add(Box.createVerticalStrut(10)); // เว้น 10 พิกเซล

        JButton menuBtn = buildButton("Back to menu"); // สร้างปุ่ม "Back to menu"
        menuBtn.addActionListener(e -> backToMain()); // กดแล้วกลับเมนูหลัก
        root.add(menuBtn); // ใส่ปุ่มลงแผง

        setContentPane(root); // ให้แผงหลักเป็นเนื้อหาของหน้าต่าง
        setDefaultCloseOperation(EXIT_ON_CLOSE); // กดปิดหน้าต่างแล้วโปรแกรมจบ
        setSize(1174, 836); // ขนาดหน้าต่าง
        setMinimumSize(new Dimension(420, 680)); // ย่อหน้าต่างได้เล็กสุด 420 x 680
        setLocationRelativeTo(null); // วางกลางจอ

    }

    // buildScoreCard = สร้างการ์ดสีเทาอ่อนที่โชว์คะแนนและเวลาที่ใช้
    private JPanel buildScoreCard() {
        JPanel card = new JPanel(); // สร้างแผงการ์ด
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // เรียงของในการ์ดจากบนลงล่าง
        card.setBackground(new Color(0xF1F1F1)); // พื้นเทาอ่อน
        card.setBorder(new EmptyBorder(14, 20, 14, 20)); // เว้นขอบในการ์ด
        card.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดการ์ดให้อยู่กลาง
        card.setMaximumSize(new Dimension(300, 110)); // จำกัดขนาดสูงสุดของการ์ด

        JLabel caption = new JLabel("Your Score", SwingConstants.CENTER); // ป้ายหัวการ์ด
        caption.setFont(new Font("SaneSerif", Font.PLAIN, 12)); // ฟอนต์ปกติ ขนาด 12 (สะกดชื่อฟอนต์ผิดเช่นเดียวกัน)
        caption.setForeground(new Color(0x77776F)); // สีเทา
        caption.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง

        JLabel scoreLabel = new JLabel(result.getScore() + " pts", SwingConstants.CENTER); // ป้ายคะแนน เช่น "450 pts"
        scoreLabel.setForeground(new Color(0x1F1F1E)); // สีเกือบดำ
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง

        JLabel timeLabel = new JLabel("Time: " + formatTime(result.getTimeUsed()), SwingConstants.CENTER); // ป้ายเวลาที่ใช้ เช่น "Time: 1:32"
        timeLabel.setFont(new Font("Saneserif", Font.PLAIN, 12)); // ฟอนต์ปกติ ขนาด 12
        timeLabel.setForeground(new Color(0x77776F)); // สีเทา
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง

        card.add(caption);                      // ใส่ป้ายหัวการ์ด
        card.add(Box.createVerticalStrut(8));   // เว้น 8 พิกเซล
        card.add(scoreLabel);                   // ใส่ป้ายคะแนน
        card.add(Box.createVerticalStrut(8));   // เว้น 8 พิกเซล
        card.add(timeLabel);                    // ใส่ป้ายเวลา
        return card; // ส่งการ์ดที่ประกอบเสร็จกลับไป
    }

    // buildButton = สร้างปุ่มสไตล์เดียวกันของหน้านี้ (พื้นเทาเข้ม ตัวอักษรขาว)
    private JButton buildButton(String text) {
        JButton b = new JButton(text); // สร้างปุ่มพร้อมข้อความ
        b.setFont(new Font("SaneSerif", Font.PLAIN, 15)); // ฟอนต์ปกติ ขนาด 15
        b.setForeground(Color.WHITE); // ตัวอักษรสีขาว
        b.setBackground(new Color(0x4F4F4F)); // พื้นปุ่มสีเทาเข้ม
        b.setOpaque(true); // ให้พื้นปุ่มทึบ (เห็นสีพื้นจริง)
        b.setBorderPainted(false); // ไม่วาดเส้นขอบ
        b.setFocusPainted(false); // ไม่วาดกรอบโฟกัสตอนกด
        b.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
        b.setMaximumSize(new Dimension(260, 42)); // ขนาดสูงสุดของปุ่ม
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เมาส์ชี้แล้วเป็นรูปมือ
        return b; // ส่งปุ่มกลับไป

    }

    // buildScoreboardPanel = สร้างแผงตารางคะแนนสูงสุดของระดับ d
    private JPanel buildScoreboardPanel(Difficulty d) {
        JPanel panel = new JPanel(); // สร้างแผง
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // เรียงจากบนลงล่าง
        panel.setBackground(Color.WHITE); // พื้นขาว
        panel.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
        panel.setMaximumSize(new Dimension(320, 210)); // จำกัดขนาดสูงสุด

        JLabel header = new JLabel("Top Scores (" + d.name() + ")", SwingConstants.CENTER); // หัวตาราง เช่น "Top Scores (EASY)"
        header.setFont(new Font("SansSerif", Font.BOLD, 14)); // ตัวหนา ขนาด 14
        header.setForeground(new Color(0x333333)); // สีเทาเข้ม
        header.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
        panel.add(header); // ใส่หัวตาราง
        panel.add(Box.createVerticalStrut(8)); // เว้น 8 พิกเซล

        java.util.List<GameResult> topScores = csvManager.readScores(d); // อ่านคะแนนของระดับ d (เรียงมากไปน้อยแล้ว)
        if (topScores == null || topScores.isEmpty()) { // ถ้ายังไม่มีคะแนนเลย
            JLabel noData = new JLabel("No records yet", SwingConstants.CENTER); // ข้อความ "ยังไม่มีบันทึก"
            noData.setFont(new Font("SansSerif", Font.ITALIC, 12)); // ตัวเอียง
            noData.setForeground(Color.GRAY); // สีเทา
            noData.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
            panel.add(noData); // ใส่ข้อความ
        } else { // ถ้ามีคะแนน
            int count = 0; // ตัวนับว่าแสดงไปกี่แถวแล้ว
            for (GameResult r : topScores) { // วนดูผลทีละรายการ
                if (count >= TOP_COUNT) // ถ้าแสดงครบ 5 แถวแล้ว
                    break;              // เลิกวน
                JPanel row = new JPanel(new BorderLayout()); // แถว 1 แถว (วางของชิดซ้ายและขวาได้)
                row.setBackground(new Color(0xFAFAFA)); // พื้นเทาอ่อนมาก
                row.setBorder(new EmptyBorder(4, 8, 4, 8)); // เว้นขอบในแถว
                row.setMaximumSize(new Dimension(300, 24)); // จำกัดขนาดแถว

                JLabel nameLabel = new JLabel((count + 1) + ". " + r.getPlayerName()); // ป้ายอันดับ+ชื่อ เช่น "1. ploy"
                nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12)); // ฟอนต์ปกติ

                JLabel detailLabel = new JLabel(r.getScore() + " pts (" + formatTime(r.getTimeUsed()) + ")"); // ป้ายคะแนน+เวลา เช่น "450 pts (1:32)"
                detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12)); // ฟอนต์ปกติ
                detailLabel.setForeground(new Color(0x555555)); // สีเทา

                row.add(nameLabel, BorderLayout.WEST);   // วางชื่อชิดซ้ายของแถว
                row.add(detailLabel, BorderLayout.EAST); // วางคะแนนชิดขวาของแถว

                panel.add(row); // ใส่แถวลงแผง
                panel.add(Box.createVerticalStrut(4)); // เว้น 4 พิกเซลระหว่างแถว
                count++; // นับแถวที่แสดงไปเพิ่มอีก 1
            }
        }
        return panel; // ส่งแผงตารางกลับไป
    }

    // formatTime = แปลงวินาทีเป็นรูปแบบ นาที:วินาที เช่น 92 -> "1:32"
    private String formatTime(int seconds) {
        int m = seconds / 60; // หารด้วย 60 ได้จำนวนนาที (หารเลขจำนวนเต็มตัดเศษทิ้ง)
        int s = seconds % 60; // เศษที่เหลือคือวินาที
        return m + ":" + String.format("%02d", s); // ต่อข้อความ โดยบังคับวินาทีให้มี 2 หลัก (เติม 0 ข้างหน้า)
    }

    // displayResult และ displayScoreboard ด้านล่างเป็นโค้ดเก่าที่ไม่มีที่ไหนเรียกใช้แล้ว ลบทิ้งได้
    public void displayResult() {
        add(new JLabel("Player: " + result.getPlayerName())); // เพิ่มป้ายชื่อผู้เล่นลงหน้าต่าง
        add(new JLabel("Score: " + result.getScore()));       // เพิ่มป้ายคะแนน
        add(new JLabel("Time usde: " + result.getTimeUsed() + "s")); // เพิ่มป้ายเวลา (คำว่า usde สะกดผิดจาก used)
    }

    public void displayScoreboard(Difficulty d) {  // เมธอดเก่า ไม่มีที่ไหนเรียกใช้แล้ว
        java.util.List<GameResult> top = csvManager.readScores(d); // อ่านคะแนนของระดับ d
        add(new JLabel("====Top score(" + d + ")---")); // เพิ่มป้ายหัวข้อตารางลงหน้าต่าง

    }

    // backToMain = ปิดหน้านี้ แล้วเปิดเมนูหลักของผู้เล่นคนเดิม
    public void backToMain() {
        dispose(); // ปิดหน้าสรุปผล
        new MainFrame(user).setVisible(true); // เปิดเมนูหลักและแสดงบนจอ
    }
   
    
}