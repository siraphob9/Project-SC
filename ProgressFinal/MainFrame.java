import java.awt.*;                  // เครื่องมือวาด/จัดหน้าจอ (Color, Font, Graphics, Dimension, BorderLayout, Cursor ...)
import javax.swing.*;               // ชิ้นส่วนหน้าต่าง Swing (JFrame, JPanel, JButton, JLabel, JScrollPane ...)
import java.util.List;              // List = รายการของสิ่งของหลายชิ้น (เจาะจงตัวนี้ ไม่ให้ชนกับ List ของ java.awt)
import javax.swing.border.EmptyBorder; // ขอบว่างเปล่า (ใช้เว้นระยะรอบๆ)

// Owner: คนที่ 3 (Menu & Game Logic)
// คลาส MainFrame = "เมนูหลัก" และหน้าย่อยอื่นๆ ได้แก่ เลือกความยาก, เลือกดูคะแนน, ตารางคะแนน
// (หน้าย่อยเหล่านั้นถูกสร้างเป็น JFrame ใหม่ทุกครั้ง ไม่ได้อยู่ในหน้าต่างเดียวกัน)
public class MainFrame extends JFrame {

    private User user; // ผู้เล่นที่ล็อกอินอยู่ (ส่งต่อไปยังหน้าต่างๆ ที่เปิดต่อ)

    // constructor: ประกอบหน้าเมนูหลัก
    public MainFrame(User user) {
        this.user = user; // เก็บผู้เล่นไว้ใช้ต่อ
        // ป้องกัน Error หากไม่มี User (เช่นตอนรันทดสอบ)

        setTitle("PhotoHunt - Main Menu"); // ตั้งชื่อบนแถบหัวหน้าต่าง
        setSize(1174, 836);                // ขนาดหน้าต่างทั้งหมด
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // กดปิดแล้วโปรแกรมจบ
        setLocationRelativeTo(null); // จัดให้อยู่กลางจอ

        // ใช้พื้นหลังสีขาวเพื่อให้ปุ่มกลมกลืน
        getContentPane().setBackground(Color.WHITE); // ตั้งพื้นหลังเนื้อหาเป็นสีขาว

        JPanel panel = new JPanel(); // แผงที่จะใส่หัวข้อและปุ่มทั้งหมด
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // เรียงของในแผงจากบนลงล่าง
        panel.setBackground(Color.WHITE); // พื้นขาว
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // เว้นขอบรอบแผง 40 พิกเซลทุกด้าน

        // ถ้ามีผู้เล่นและมีชื่อ ใช้ชื่อนั้น ถ้าไม่มีใช้คำว่า "Player" (กัน error)
        String displayName = (user != null && user.getUsername() != null ? user.getUsername() : "Player");
        JLabel title = new JLabel("Hello," + displayName, SwingConstants.CENTER); // หัวข้อต้อนรับ เช่น "Hello,ploy"
        title.setFont(new Font("SansSerif", Font.BOLD, 22)); // ตัวหนาขนาด 22
        title.setForeground(new Color(0x1F1F1E)); // สีเกือบดำ
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกึ่งกลางแนวนอน

        JLabel subtitle = new JLabel("Welcome to Photo Hunt", SwingConstants.CENTER); // ข้อความใต้หัวข้อ
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14)); // ตัวปกติขนาด 14
        subtitle.setForeground(new Color(0x77776F)); // สีเทา
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง

        // หัวข้อรอง
        JButton playBtn = createMenuButton("Play Game");    // ปุ่มเริ่มเล่น
        JButton scoreBtn = createMenuButton("View Scores"); // ปุ่มดูคะแนน
        JButton exitBtn = createMenuButton("Exit");         // ปุ่มออกจากโปรแกรม

        // กด Play Game: ปิดหน้านี้ แล้วเปิดหน้าเลือกความยาก
        playBtn.addActionListener(e -> {
            this.dispose();             // ปิดหน้าเมนูหลัก
            openDifficultySelection();  // เปิดหน้าเลือกความยาก
        });

        // กด View Scores: ปิดหน้านี้ แล้วเปิดหน้าเลือกหมวดคะแนน
        scoreBtn.addActionListener(e -> {
            this.dispose();            // ปิดหน้าเมนูหลัก
            openScoreboardDialog();    // เปิดหน้าเลือกหมวดคะแนน
        });

        exitBtn.addActionListener(e -> System.exit(0)); // กด Exit: ปิดโปรแกรมทันที (0 = จบแบบปกติ)
        panel.add(Box.createVerticalStrut(180)); // ช่องว่างแนวตั้ง 180 พิกเซล (ดันเนื้อหาลงมาให้อยู่กลางหน้า)
        panel.add(title);                        // ใส่หัวข้อ
        panel.add(Box.createVerticalStrut(5));   // เว้น 5 พิกเซล
        panel.add(subtitle);                     // ใส่ข้อความใต้หัวข้อ
        panel.add(Box.createVerticalStrut(40));  // เว้น 40 พิกเซล

        panel.add(playBtn);                      // ใส่ปุ่ม Play Game
        panel.add(Box.createVerticalStrut(30));  // เว้น 30 พิกเซล
        panel.add(scoreBtn);                     // ใส่ปุ่ม View Scores
        panel.add(Box.createVerticalStrut(30));  // เว้น 30 พิกเซล
        panel.add(exitBtn);                      // ใส่ปุ่ม Exit

        add(panel); // ใส่แผงนี้ลงหน้าต่าง

    }

    // openDifficultySelection = เปิดหน้าเลือกระดับความยาก (เป็น JFrame ใหม่)
    private void openDifficultySelection() {
        JFrame diffFrame = new JFrame("Select Difficulty"); // สร้างหน้าต่างใหม่ ตั้งชื่อ
        diffFrame.setSize(1174, 836);                       // ขนาดหน้าต่าง
        diffFrame.setLocationRelativeTo(this);              // วางกลางตามหน้าต่างเมนูหลัก
        diffFrame.getContentPane().setBackground(Color.WHITE); // พื้นหลังขาว

        // สร้างแผงที่เขียนทับ paintComponent เพื่อเปิดโหมดตัวอักษรเนียน (ตอนนี้ไม่ได้วาดอะไรเพิ่ม)
        JPanel panel = new JPanel() {
            @Override  // บอกว่าเขียนทับเมธอดวาดของแผงเดิม
            protected void paintComponent(Graphics g) {  // เมธอดวาดพื้นหลังแผง (Swing เรียกเอง)
                super.paintComponent(g); // ให้แผงวาดพื้นหลังตามปกติ
                Graphics2D g2 = (Graphics2D) g.create(); // ทำสำเนาพู่กัน 2D
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // เปิดตัวอักษรเนียน
                g2.dispose(); // คืนพู่กัน
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // เรียงจากบนลงล่าง
        panel.setBackground(Color.WHITE); // พื้นขาว
        panel.setBorder(new EmptyBorder(40, 40, 40, 40)); // เว้นขอบรอบแผง

        JLabel title = new JLabel("Select Difficulty", SwingConstants.CENTER); // หัวข้อ
        title.setFont(new Font("SansSerif", Font.BOLD, 20)); // ตัวหนาขนาด 20
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง

        JButton easyBtn = createDifficultyButton("Easy");     // ปุ่ม Easy
        JButton normalBtn = createDifficultyButton("Normal"); // ปุ่ม Normal
        JButton hardBtn = createDifficultyButton("Hard");     // ปุ่ม Hard

        // กดปุ่มแต่ละระดับ: ปิดหน้านี้ แล้วเริ่มเกมด้วยระดับนั้น
        easyBtn.addActionListener(e -> {
            diffFrame.dispose();             // ปิดหน้าเลือกความยาก
            startGame(Difficulty.EASY);      // เริ่มเกมระดับ Easy
        });
        normalBtn.addActionListener(e -> {  // กดปุ่ม Normal ให้ทำตามก้อนคำสั่งข้างล่าง
            diffFrame.dispose();             // ปิดหน้าเลือกความยาก
            startGame(Difficulty.NORMAL);    // เริ่มเกมระดับ Normal
        });
        hardBtn.addActionListener(e -> {  // กดปุ่ม Hard ให้ทำตามก้อนคำสั่งข้างล่าง
            diffFrame.dispose();             // ปิดหน้าเลือกความยาก
            startGame(Difficulty.HARD);      // เริ่มเกมระดับ Hard
        });

        panel.add(Box.createVerticalStrut(180)); // ช่องว่างด้านบน
        panel.add(title);                        // ใส่หัวข้อ
        panel.add(Box.createVerticalStrut(30));  // เว้น 30 พิกเซล
        panel.add(easyBtn);                      // ใส่ปุ่ม Easy
        panel.add(Box.createVerticalStrut(15));  // เว้น 15 พิกเซล
        panel.add(normalBtn);                    // ใส่ปุ่ม Normal
        panel.add(Box.createVerticalStrut(15));  // เว้น 15 พิกเซล
        panel.add(hardBtn);                      // ใส่ปุ่ม Hard

        diffFrame.add(panel);          // ใส่แผงลงหน้าต่าง
        diffFrame.setVisible(true);    // แสดงหน้าต่างบนจอ
    }

    // openScoreboardDialog = เปิดหน้าเลือกว่าจะดูคะแนนของระดับไหน
    private void openScoreboardDialog() {
        JFrame scoreChoiceFrame = new JFrame("PhotoHunt - View Scores"); // สร้างหน้าต่างใหม่
        scoreChoiceFrame.setSize(1174, 836); // ขนาดหน้าต่าง
        scoreChoiceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // กดปิดแล้วปิดแค่หน้านี้ ไม่ปิดทั้งโปรแกรม
        scoreChoiceFrame.setLocationRelativeTo(null); // วางกลางจอ
        scoreChoiceFrame.getContentPane().setBackground(Color.WHITE); // พื้นหลังขาว

        JPanel panel = new JPanel(); // แผงใส่หัวข้อและปุ่ม
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // เรียงจากบนลงล่าง
        panel.setBackground(Color.WHITE); // พื้นขาว
        panel.setBorder(new EmptyBorder(40, 40, 40, 40)); // เว้นขอบรอบแผง

        JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER); // หัวข้อ "Leaderboard"
        title.setFont(new Font("SansSerif", Font.BOLD, 22)); // ตัวหนาขนาด 22
        title.setForeground(new Color(0x1F1F1E)); // สีเกือบดำ
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง

        JLabel subtitle = new JLabel("Select difficulty category", SwingConstants.CENTER); // คำบรรยาย
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14)); // ตัวปกติขนาด 14
        subtitle.setForeground(new Color(0x77776F)); // สีเทา
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง

        JButton easyScoreBtn = createModernButton("Easy Scores");     // ปุ่มดูคะแนน Easy
        JButton normalScoreBtn = createModernButton("Normal Scores"); // ปุ่มดูคะแนน Normal
        JButton hardScoreBtn = createModernButton("Hard Scores");     // ปุ่มดูคะแนน Hard
        JButton backBtn = createSecondaryButton("Back to Menu");      // ปุ่มกลับเมนู

        // กดปุ่มแต่ละระดับ: ปิดหน้านี้ แล้วเปิดตารางคะแนนของระดับนั้น
        easyScoreBtn.addActionListener(e -> {
            scoreChoiceFrame.dispose();       // ปิดหน้าเลือกหมวด
            showScoreWindow(Difficulty.EASY); // เปิดตารางคะแนน Easy
        });
        normalScoreBtn.addActionListener(e -> {  // กดปุ่ม Normal Scores ให้ทำตามก้อนคำสั่งข้างล่าง
            scoreChoiceFrame.dispose();         // ปิดหน้าเลือกหมวด
            showScoreWindow(Difficulty.NORMAL); // เปิดตารางคะแนน Normal
        });
        hardScoreBtn.addActionListener(e -> {  // กดปุ่ม Hard Scores ให้ทำตามก้อนคำสั่งข้างล่าง
            scoreChoiceFrame.dispose();       // ปิดหน้าเลือกหมวด
            showScoreWindow(Difficulty.HARD); // เปิดตารางคะแนน Hard
        });
        // กด Back to Menu: ปิดหน้านี้ แล้วเปิดเมนูหลักใหม่
        backBtn.addActionListener(e -> {
            scoreChoiceFrame.dispose();            // ปิดหน้าเลือกหมวด
            new MainFrame(user).setVisible(true); // กลับมาหน้า Main
        });

        panel.add(Box.createVerticalStrut(180)); // ช่องว่างด้านบน
        panel.add(title);                        // ใส่หัวข้อ
        panel.add(Box.createVerticalStrut(5));   // เว้น 5 พิกเซล
        panel.add(subtitle);                     // ใส่คำบรรยาย
        panel.add(Box.createVerticalStrut(30));  // เว้น 30 พิกเซล
        panel.add(easyScoreBtn);                 // ใส่ปุ่ม Easy Scores
        panel.add(Box.createVerticalStrut(12));  // เว้น 12 พิกเซล
        panel.add(normalScoreBtn);               // ใส่ปุ่ม Normal Scores
        panel.add(Box.createVerticalStrut(12));  // เว้น 12 พิกเซล
        panel.add(hardScoreBtn);                 // ใส่ปุ่ม Hard Scores
        panel.add(Box.createVerticalStrut(20));  // เว้น 20 พิกเซล
        panel.add(backBtn);                      // ใส่ปุ่ม Back to Menu

        scoreChoiceFrame.add(panel);          // ใส่แผงลงหน้าต่าง
        scoreChoiceFrame.setVisible(true);    // แสดงหน้าต่างบนจอ
    }

    // showScoreWindow = เปิดหน้าต่างตารางคะแนน (Top 10) ของระดับ d
    private void showScoreWindow(Difficulty d) {
        JFrame scoreWindow = new JFrame("Top Scores - " + d.name()); // สร้างหน้าต่างใหม่ ชื่อมีชื่อระดับต่อท้าย
        scoreWindow.setSize(1174, 836); // ขนาดหน้าต่าง
        scoreWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // กดปิดแล้วปิดแค่หน้านี้
        scoreWindow.setLocationRelativeTo(null); // วางกลางจอ
        scoreWindow.getContentPane().setBackground(Color.WHITE); // พื้นหลังขาว

        JPanel panel = new JPanel(); // แผงหลักของหน้านี้
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // เรียงจากบนลงล่าง
        panel.setBackground(Color.WHITE); // พื้นขาว
        panel.setBorder(new EmptyBorder(30, 30, 30, 30)); // เว้นขอบรอบแผง

        JLabel title = new JLabel("Top Scores (" + d.name() + ")", SwingConstants.CENTER); // หัวข้อ เช่น "Top Scores (EASY)"
        title.setFont(new Font("SansSerif", Font.BOLD, 20)); // ตัวหนาขนาด 20
        title.setForeground(new Color(0x1F1F1E)); // สีเกือบดำ
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
        panel.add(title); // ใส่หัวข้อ
        panel.add(Box.createVerticalStrut(20)); // เว้น 20 พิกเซล

        CSVManager csv = new CSVManager(); // สร้างตัวอ่านไฟล์
        List<GameResult> scores = csv.readScores(d); // อ่านคะแนนของระดับ d (เรียงมากไปน้อยแล้ว)

        JPanel listPanel = new JPanel(); // แผงสำหรับใส่รายการคะแนนทั้งหมด
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); // เรียงจากบนลงล่าง
        listPanel.setBackground(Color.WHITE); // พื้นขาว

        if (scores == null || scores.isEmpty()) { // ถ้ายังไม่มีคะแนนเลย
            JLabel emptyLabel = new JLabel("No records found.", SwingConstants.CENTER); // ข้อความ "ไม่พบบันทึก"
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // ตัวปกติขนาด 14
            emptyLabel.setForeground(new Color(0xAAAAAA)); // สีเทาอ่อน
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
            listPanel.add(emptyLabel); // ใส่ข้อความลงรายการ
        } else { // ถ้ามีคะแนน
            int rank = 1; // ตัวนับอันดับ เริ่มที่ 1
            for (GameResult r : scores) { // วนดูผลทีละรายการ (เรียงจากคะแนนสูงไปต่ำ)
                if (rank > 10) // ถ้าเกิน 10 อันดับแล้ว
                    break; // แสดง Top 10 เลิกวน

                JPanel rowPanel = new JPanel(new BorderLayout()); // แถว 1 แถว (วางของชิดซ้ายและขวาได้)
                rowPanel.setBackground(new Color(0xF9F9F9)); // พื้นเทาอ่อนมาก
                // ขอบแถวแบบรวม: เส้นบางๆ ด้านล่าง + เว้นขอบใน 10/15 พิกเซล
                rowPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEEEEEE)),  // ต่อ: เส้นบางสีเทาอ่อนด้านล่างแถว
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));  // ต่อ: เว้นขอบด้านในของแถว
                rowPanel.setMaximumSize(new Dimension(340, 45)); // จำกัดขนาดแถวสูงสุด

                JLabel nameLabel = new JLabel(rank + ". " + r.getPlayerName()); // ป้ายอันดับ+ชื่อ เช่น "1. ploy"
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13)); // ตัวหนาขนาด 13
                nameLabel.setForeground(new Color(0x333333)); // สีเทาเข้ม

                // ป้ายคะแนน+เวลา เช่น "450 pts (1:32)"
                JLabel statLabel = new JLabel(r.getScore() + " pts (" + formatTime(r.getTimeUsed()) + ")");
                statLabel.setFont(new Font("SansSerif", Font.PLAIN, 13)); // ตัวปกติขนาด 13
                statLabel.setForeground(new Color(0x666666)); // สีเทา

                rowPanel.add(nameLabel, BorderLayout.WEST);  // วางชื่อชิดซ้ายของแถว
                rowPanel.add(statLabel, BorderLayout.EAST);  // วางคะแนนชิดขวาของแถว

                listPanel.add(rowPanel); // ใส่แถวลงรายการ
                listPanel.add(Box.createVerticalStrut(6)); // เว้น 6 พิกเซลระหว่างแถว
                rank++; // เพิ่มอันดับถัดไป
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel); // ห่อรายการด้วยแผงเลื่อนได้ (ถ้ายาวเกินหน้าจอ)
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // ไม่มีเส้นขอบ
        scrollPane.getViewport().setBackground(Color.WHITE); // พื้นหลังส่วนที่มองเห็นเป็นสีขาว
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง

        JButton backBtn = createSecondaryButton("Back"); // ปุ่ม Back
        // กด Back: ปิดหน้านี้ แล้วกลับไปหน้าเลือกหมวดคะแนน
        backBtn.addActionListener(e -> {
            scoreWindow.dispose();   // ปิดหน้าตารางคะแนน
            openScoreboardDialog();  // กลับไปหน้าเลือกหมวดคะแนน
        });

        panel.add(scrollPane);                  // ใส่แผงเลื่อนลงแผงหลัก
        panel.add(Box.createVerticalStrut(20)); // เว้น 20 พิกเซล
        panel.add(backBtn);                     // ใส่ปุ่ม Back

        scoreWindow.add(panel);        // ใส่แผงหลักลงหน้าต่าง
        scoreWindow.setVisible(true);  // แสดงหน้าต่างบนจอ
    }

    

    // formatTime = แปลงวินาทีเป็นรูปแบบ นาที:วินาที เช่น 92 -> "1:32"
    private String formatTime(int seconds) {
        int m = seconds / 60; // หารด้วย 60 ได้จำนวนนาที (ตัดเศษทิ้ง)
        int s = seconds % 60; // เศษที่เหลือคือวินาที
        return m + ":" + String.format("%02d", s); // ต่อข้อความ โดยบังคับวินาทีให้มี 2 หลัก (เติม 0 ข้างหน้า)
    }

    // createMenuButton = สร้างปุ่มเมนูหลัก (พื้นเทาเข้ม ตัวอักษรขาว)
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text); // สร้างปุ่มพร้อมข้อความ
        btn.setFont(new Font("SansSerif", Font.PLAIN, 15)); // ฟอนต์ปกติขนาด 15
        btn.setForeground(Color.WHITE); // ตัวอักษรขาว
        btn.setBackground(new Color(0x4F4F4F)); // พื้นเทาเข้ม
        btn.setOpaque(true); // ให้พื้นปุ่มทึบ
        btn.setBorderPainted(false); // ไม่วาดเส้นขอบ
        btn.setFocusPainted(false); // ไม่วาดกรอบโฟกัส
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
        btn.setMaximumSize(new Dimension(280, 42)); // ขนาดสูงสุด 280 x 42
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เมาส์ชี้แล้วเป็นรูปมือ
        return btn; // ส่งปุ่มกลับไป
    }

    // createDifficultyButton = สร้างปุ่มเลือกระดับ พื้นขาว มีกรอบบาง ข้อความซ้าย ลูกศร > ขวา
    private JButton createDifficultyButton(String text) {
        JButton btn = new JButton(); // สร้างปุ่มเปล่า (จะใส่ป้ายข้างในเอง)
        btn.setLayout(new BorderLayout()); // ให้ข้างในปุ่มวางของแบบ ซ้าย/ขวา ได้

        JLabel textLabel = new JLabel("   " + text); // ป้ายชื่อระดับ (เว้นวรรคหน้า 3 ตัวเพื่อไม่ให้ชิดขอบ)
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // ฟอนต์ปกติขนาด 14

        JLabel arrowLabel = new JLabel(">   "); // ป้ายลูกศร (เว้นวรรคหลัง 3 ตัว)
        arrowLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // ฟอนต์ปกติขนาด 14

        btn.add(textLabel, BorderLayout.WEST);  // วางป้ายชื่อชิดซ้ายในปุ่ม
        btn.add(arrowLabel, BorderLayout.EAST); // วางลูกศรชิดขวาในปุ่ม

        btn.setBackground(Color.WHITE); // พื้นปุ่มสีขาว
        btn.setFocusPainted(false); // ไม่วาดกรอบโฟกัส
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
        btn.setPreferredSize(new Dimension(260, 42)); // ขนาดที่ปุ่มอยากได้
        btn.setMaximumSize(new Dimension(260, 42));   // ขนาดสูงสุด
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); // เส้นขอบสีเทาอ่อน หนา 1 พิกเซล
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เมาส์เป็นรูปมือ

        return btn; // ส่งปุ่มกลับไป
    }

    // createModernButton = ปุ่มสไตล์เดียวกับปุ่มเมนู ใช้ในหน้าเลือกหมวดคะแนน (มีกำหนด PreferredSize เพิ่ม)
    private JButton createModernButton(String text) {
        JButton btn = new JButton(text); // สร้างปุ่มพร้อมข้อความ
        btn.setFont(new Font("SansSerif", Font.PLAIN, 15)); // ฟอนต์ปกติขนาด 15
        btn.setForeground(Color.WHITE); // ตัวอักษรขาว
        btn.setBackground(new Color(0x4F4F4F)); // พื้นเทาเข้ม
        btn.setOpaque(true); // พื้นทึบ
        btn.setBorderPainted(false); // ไม่วาดเส้นขอบ
        btn.setFocusPainted(false); // ไม่วาดกรอบโฟกัส
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
        btn.setMaximumSize(new Dimension(280, 42));   // ขนาดสูงสุด
        btn.setPreferredSize(new Dimension(280, 42)); // ขนาดที่ปุ่มอยากได้
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เมาส์เป็นรูปมือ
        return btn; // ส่งปุ่มกลับไป
    }

    // createSecondaryButton = ปุ่มรอง (เช่น Back) พื้นเทาอ่อน ตัวอักษรเทา
    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text); // สร้างปุ่มพร้อมข้อความ
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14)); // ฟอนต์ปกติขนาด 14
        btn.setForeground(new Color(0x77776F)); // ตัวอักษรสีเทา
        btn.setBackground(new Color(0xEEEEEE)); // พื้นเทาอ่อน
        btn.setOpaque(true); // พื้นทึบ
        btn.setBorderPainted(false); // ไม่วาดเส้นขอบ
        btn.setFocusPainted(false); // ไม่วาดกรอบโฟกัส
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); // จัดกลาง
        btn.setMaximumSize(new Dimension(280, 38));   // ขนาดสูงสุด
        btn.setPreferredSize(new Dimension(280, 38)); // ขนาดที่ปุ่มอยากได้
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เมาส์เป็นรูปมือ
        return btn; // ส่งปุ่มกลับไป
    }

    // startGame = เริ่มเกมด้วยระดับ d: ปิดหน้านี้ สร้าง session ใหม่ แล้วเปิดหน้าเล่นเกม
    private void startGame(Difficulty d) {
        this.dispose(); // ปิดหน้าต่างนี้
        GameSession session = new GameSession(user, d); // สร้างเกมรอบใหม่ (สุ่มรูปและจุดต่างตามระดับ)
        System.out.println("====== START GAME SESSION ======"); // พิมพ์ข้อความลงคอนโซลไว้ตรวจดูตอนพัฒนา
        System.out.println("Difficulty Selected: " + d.name()); // พิมพ์ระดับที่เลือก
        new GameFrame(session).setVisible(true); // สร้างหน้าเล่นเกมและแสดงบนจอ
    }
}