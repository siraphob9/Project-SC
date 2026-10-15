import java.awt.*;          // เครื่องมือวาด/จัดหน้าจอพื้นฐาน (Color, Font, Graphics, BorderLayout, GridLayout ...)
import java.awt.event.*;    // เครื่องมือรับเหตุการณ์ เช่น คลิกเมาส์ (MouseAdapter, MouseEvent)
import java.awt.image.*;    // เครื่องมือเกี่ยวกับรูป (BufferedImage)
import javax.swing.*;       // ชิ้นส่วนหน้าต่าง Swing (JFrame, JPanel, JLabel, Timer ...)

// คลาส GameFrame = "หน้าต่างเล่นเกม" (extends JFrame = สืบทอดความเป็นหน้าต่างมาจาก JFrame)
public class GameFrame extends JFrame {
    private GameSession session;     // ผู้ควบคุมกติกาของเกมรอบนี้
    private ImagePanel leftPanel;    // ช่องแสดงรูปซ้าย
    private ImagePanel rightPanel;   // ช่องแสดงรูปขวา
    private JLabel timeLabel;        // ป้ายแสดงเวลาที่เหลือ
    private JLabel foundLabel;       // ป้ายแสดงจำนวนจุดที่เจอ
    private JLabel missLabel;        // ป้ายแสดงจำนวนครั้งที่พลาด
    private Timer timer;             // นาฬิกาที่เดินทุกวินาที (javax.swing.Timer)
    private boolean resultOpened = false; // เปิดหน้าสรุปผลไปแล้วหรือยัง (กันเปิดซ้ำ)
    private boolean transitioning = false; // กำลังรอเปลี่ยนไปหน้าสรุปผลอยู่หรือไม่ (ช่วงนี้คลิกไม่นับ)

    // constructor: สร้างหน้าต่างเกม รับ session ที่เตรียมไว้แล้ว
    public GameFrame(GameSession session) {
        this.session = session;                       // เก็บ session ไว้ใช้ทั้งคลาส
        setTitle("Photo Hunt");                       // ตั้งชื่อบนแถบหัวหน้าต่าง
        setSize(1174, 836);                           // ตั้งขนาดหน้าต่างทั้งหมด (กว้าง x สูง)
        setDefaultCloseOperation(EXIT_ON_CLOSE);      // กดปิดหน้าต่างแล้วให้โปรแกรมจบทั้งหมด
        setLocationRelativeTo(null);                  // วางหน้าต่างไว้กลางจอ
        setLayout(new BorderLayout());                // แบ่งหน้าต่างเป็นโซน เหนือ/ใต้/ซ้าย/ขวา/กลาง

        // TODO: สร้าง UI (2 panel แสดงรูป, label เวลา/คะแนน/พลาด) + Timer
        JPanel top = new JPanel(new GridLayout(1, 3)); // แถบด้านบน แบ่งเป็น 1 แถว 3 ช่องเท่าๆ กัน
        //roundLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel = new JLabel("", SwingConstants.CENTER);  // สร้างป้ายเวลา ข้อความเริ่มว่าง จัดกลางช่อง
        foundLabel = new JLabel("", SwingConstants.CENTER); // สร้างป้ายจำนวนที่เจอ
        missLabel = new JLabel("", SwingConstants.CENTER);  // สร้างป้ายจำนวนที่พลาด
        for (JLabel l : new JLabel[] {  timeLabel, foundLabel, missLabel }) { // วนทำกับป้ายทั้ง 3 อัน
            l.setFont(l.getFont().deriveFont(Font.BOLD, 16)); // ตั้งฟอนต์ตัวหนา ขนาด 16
            top.add(l);                                       // ใส่ป้ายลงในแถบด้านบน
        }
        add(top, BorderLayout.NORTH); // วางแถบนี้ไว้ด้านบนสุดของหน้าต่าง

        leftPanel = new ImagePanel();  // สร้างช่องรูปซ้าย
        rightPanel = new ImagePanel(); // สร้างช่องรูปขวา
        JPanel center = new JPanel(new GridLayout(1, 2, 8, 0)); // แผงตรงกลาง 1 แถว 2 ช่อง เว้นระยะห่างระหว่างช่อง 8 พิกเซล
        center.add(leftPanel);  // ใส่ช่องซ้าย
        center.add(rightPanel); // ใส่ช่องขวา
        add(center, BorderLayout.CENTER); // วางแผงนี้ไว้ตรงกลางหน้าต่าง (ได้พื้นที่ที่เหลือทั้งหมด)

        // สร้างนาฬิกา: ทำงานทุก 1000 มิลลิวินาที (= 1 วินาที) ตามก้อนคำสั่งใน { }
        timer = new Timer(1000, e -> {
            session.tick();    // ลดเวลาลง 1 วินาที
            updateDisplay();   // อัปเดตข้อความบนหน้าจอ
            if (session.getTimeRemaining() <= 0) { // ถ้าเวลาหมดแล้ว
                timer.stop();  // หยุดนาฬิกา
                openResult();  // ไปหน้าสรุปผล
            }
        });

        displayLevel(); // แสดงรูปและจุดต่างของรอบนี้บนหน้าจอ
        timer.start();  // เริ่มจับเวลา
    }

    /**
     * TODO: โหลดภาพ session.getCurrentPair() (ImageIO.read จากไฟล์) แสดงบน panel
     * ทั้งสองฝั่ง
     */
    // displayLevel = เอารูปและจุดต่างของรอบนี้ไปใส่ในช่องซ้าย/ขวา
    public void displayLevel() {
        PhotoPair pair = session.getPair();           // ขอคู่รูปจาก session
        leftPanel.setImage(pair.getLeft());           // ส่งรูปซ้ายให้ช่องซ้าย
        rightPanel.setImage(pair.getRight());         // ส่งรูปขวาให้ช่องขวา
        leftPanel.setSpots(session.getActiveSpots()); // ส่งรายการจุดต่างให้ช่องซ้าย (ไว้วาดวงกลมตอนเจอ)
        rightPanel.setSpots(session.getActiveSpots());// ส่งรายการจุดต่างให้ช่องขวา
        updateDisplay();                              // อัปเดตป้ายข้อความ
    }

    /** TODO: ส่งพิกัดคลิกให้ session.handleClick แล้ว updateDisplay */
    // handleClick = ถูกเรียกเมื่อผู้เล่นคลิกบนรูป (x, y คือพิกัดบนรูปต้นฉบับ)
    public void handleClick(int x, int y) {
        if (resultOpened || transitioning) // ถ้าเกมจบแล้ว หรือกำลังรอเปลี่ยนหน้าอยู่
            return;                        // ไม่ต้องทำอะไร (ไม่รับคลิกเพิ่ม)

        boolean hit = session.handleClick(x, y); // ตรวจสอบว่าคลิกโดนจุดไหม (ตัวแปร hit เก็บผลไว้แต่ไม่ได้ใช้ต่อ)
        updateDisplay();                         // อัปเดตข้อความและวาดวงกลมใหม่

        // เช็คว่าหาเจอครบทุกจุดหรือยัง
        if (session.isRoundComplete()) { // ถ้าเจอครบแล้ว
            transitioning = true;        // ตั้งธงว่ากำลังรอเปลี่ยนหน้า
            timer.stop(); // สั่งหยุดเวลาทันทีที่เล่นจบเกม

            // สร้างตัวจับเวลาอีกตัวให้รอ 1000 มิลลิวินาที (1 วินาที) ก่อนไปหน้าสรุปผล เพื่อให้เห็นวงกลมจุดสุดท้ายก่อน
            javax.swing.Timer wait = new javax.swing.Timer(1000, e -> {
                openResult(); // เรียกเปิดหน้าจอผลลัพธ์โดยตรง
            });
            wait.setRepeats(false); // ให้ทำงานครั้งเดียวพอ ไม่วนซ้ำ
            wait.start();           // เริ่มนับ 1 วินาที
        }
    }

    // openResult = บันทึกคะแนนแล้วเปิดหน้าสรุปผล พร้อมปิดหน้าเกมนี้
    public void openResult() {
        if (resultOpened) // ถ้าเปิดหน้าสรุปผลไปแล้ว
            return;       // ไม่ทำซ้ำ
        resultOpened = true; // ตั้งธงว่าเปิดแล้ว

        if (timer != null && timer.isRunning()) { // ถ้านาฬิกายังเดินอยู่
            timer.stop();                         // หยุดมัน
        }

        GameResult result = session.toResult(); // สรุปเกมรอบนี้เป็นผลลัพธ์

        try { // ลองบันทึกคะแนน
            new CSVManager().writeScore(result); // เขียนคะแนนลงไฟล์ (ถ้าเป็น Guest จะไม่ถูกบันทึกเอง)
        } catch (Exception e) { // ถ้าบันทึกพัง
            e.printStackTrace(); // พิมพ์ error ไว้ดู แต่ไม่ให้เกมล่ม
        }

        // เปิดหน้าจอ Result และปิดหน้าต่างเกมปัจจุบัน
        SwingUtilities.invokeLater(() -> { // สั่งให้ทำในจังหวะที่ Swing พร้อม
            new ResultScoreboardFrame(result, session.getUser()).setVisible(true); // สร้างและแสดงหน้าสรุปผล
            dispose(); // ปิดหน้าต่างเกมนี้
        });
    }

    /** TODO: อัปเดตเวลา / จุดที่เจอ / วงกลมทับจุดที่เจอ */
    // updateDisplay = เปลี่ยนข้อความบนป้ายให้ตรงกับสถานะล่าสุด และสั่งวาดรูปใหม่
    public void updateDisplay() {
        int total = session.getActiveSpots().size(); // จำนวนจุดต่างทั้งหมดของรอบนี้
        // ถ้ายังไม่มีระบบหลายรูป ให้แสดงแค่สถานะทั่วไปก่อน
        timeLabel.setText("Time left: " + session.getTimeRemaining() + " s");           // แสดงเวลาที่เหลือ
        foundLabel.setText("Found: " + session.getFoundCount() + " / " + total);        // แสดงเจอกี่จุดจากทั้งหมด
        missLabel.setText("Misses: " + session.getMisses());                            // แสดงจำนวนครั้งที่พลาด
        leftPanel.repaint();  // สั่งให้ช่องซ้ายวาดใหม่ (วงกลมแดงจะโผล่)
        rightPanel.repaint(); // สั่งให้ช่องขวาวาดใหม่
    }

    // ImagePanel = คลาสซ้อนข้างใน: ช่องแสดงรูป 1 ช่อง (วาดรูป วาดวงกลม และรับคลิก)
    private class ImagePanel extends JPanel {
        private BufferedImage image;                       // รูปที่จะแสดง
        private java.util.List<DifferenceSpot> spots;      // จุดต่างของรูปนี้ (ไว้วาดวงกลมตอนเจอ)
        private double scale = 1;                          // อัตราย่อ/ขยายรูปให้พอดีช่อง
        private int offX = 0;                              // ระยะเว้นด้านซ้ายของรูปในช่อง (ตอนจัดกึ่งกลาง)
        private int offY = 0;                              // ระยะเว้นด้านบนของรูปในช่อง

        // constructor ของช่องรูป
        ImagePanel() {
            setBackground(Color.DARK_GRAY); // พื้นหลังช่องสีเทาเข้ม (ส่วนที่รูปไม่เต็มช่อง)
            addMouseListener(new MouseAdapter() { // เพิ่มตัวคอยฟังเมาส์
                @Override
                public void mousePressed(MouseEvent e) { // ทำงานเมื่อกดเมาส์ลงบนช่องนี้
                    if (image == null) // ถ้ายังไม่มีรูป
                        return;        // ไม่ทำอะไร
                    // แปลงตำแหน่งคลิกบนหน้าจอ -> พิกัดบนรูปต้นฉบับ: หักระยะเว้นขอบ แล้วหารอัตราย่อ
                    int ix = (int) Math.round((e.getX() - offX) / scale);
                    int iy = (int) Math.round((e.getY() - offY) / scale);  // พิกัดแนวตั้งบนรูปต้นฉบับ (คิดแบบเดียวกับ ix)
                    // ถ้าคลิกนอกพื้นที่รูป (ค่าติดลบ หรือเกินความกว้าง/สูงของรูป)
                    if (ix < 0 || iy < 0 || ix >= image.getWidth() || iy >= image.getHeight())
                        return; // ไม่นับ
                    GameFrame.this.handleClick(ix, iy); // ส่งพิกัดบนรูปให้หน้าเกมจัดการ (GameFrame.this = หน้าเกมที่ครอบอยู่)
                }
            });
        }

        // setImage = รับรูปมาเก็บไว้ในช่องนี้
        void setImage(BufferedImage image) {
            this.image = image;  // เก็บรูปที่ส่งมาไว้ในช่องนี้
        }

        // setSpots = รับรายการจุดต่างมาเก็บ
        void setSpots(java.util.List<DifferenceSpot> spots) {
            this.spots = spots;  // เก็บรายการจุดต่างที่ส่งมา
        }

        // paintComponent = เมธอดที่ Swing เรียกทุกครั้งที่ต้องวาดช่องนี้ (@Override = เขียนทับของเดิม)
        @Override
        protected void paintComponent(Graphics g) {  // วาดช่องรูปนี้ (Swing เรียกเองเมื่อต้องวาดใหม่)
            super.paintComponent(g); // ให้คลาสแม่วาดพื้นหลังให้ก่อน
            if (image == null)       // ถ้าไม่มีรูป
                return;              // ไม่มีอะไรต้องวาด
            Graphics2D g2 = (Graphics2D) g.create(); // ทำสำเนาพู่กันไว้ใช้ (รุ่น 2D วาดได้ละเอียดกว่า)
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); // ย่อ/ขยายรูปให้เนียน
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);           // ขอบเส้นเรียบไม่เป็นขั้นบันได

            // เลือกอัตราย่อที่ทำให้รูปพอดีช่องทั้งกว้างและสูง (เอาค่าที่น้อยกว่า) โดยไม่ทำให้รูปเบี้ยว
            scale = Math.min((double) getWidth() / image.getWidth(), (double) getHeight() / image.getHeight());
            int w = (int) (image.getWidth() * scale);   // ความกว้างของรูปหลังย่อ
            int h = (int) (image.getHeight() * scale);  // ความสูงของรูปหลังย่อ
            offX = (getWidth() - w) / 2;   // ระยะเว้นซ้าย = พื้นที่ที่เหลือหารสอง (ทำให้รูปอยู่กึ่งกลางแนวนอน)
            offY = (getHeight() - h) / 2;  // ระยะเว้นบน = พื้นที่ที่เหลือหารสอง (กึ่งกลางแนวตั้ง)
            g2.drawImage(image, offX, offY, w, h, null); // วาดรูปที่ตำแหน่ง (offX, offY) ขนาด w x h

            if (spots != null) { // ถ้ามีรายการจุดต่าง
                g2.setColor(Color.RED);            // ใช้สีแดง
                g2.setStroke(new BasicStroke(3f)); // เส้นหนา 3 พิกเซล
                for (DifferenceSpot s : spots) {   // วนดูจุดต่างทีละจุด
                    if (!s.isFound())              // ถ้ายังไม่เจอ
                        continue;                  // ข้าม (ไม่วาดวงกลม)
                    int r = (int) Math.round(s.getRadius() * scale);       // รัศมีบนหน้าจอ = รัศมีจริง x อัตราย่อ
                    int cx = offX + (int) Math.round(s.getX() * scale);    // ศูนย์กลางแนวนอนบนหน้าจอ
                    int cy = offY + (int) Math.round(s.getY() * scale);    // ศูนย์กลางแนวตั้งบนหน้าจอ
                    g2.drawOval(cx - r, cy - r, 2 * r, 2 * r); // วาดวงกลม: มุมซ้ายบน = ศูนย์กลางลบรัศมี, กว้างสูง = 2 เท่าของรัศมี
                }
            }
            g2.dispose(); // คืนพู่กันที่ทำสำเนามา
        }
    }
}