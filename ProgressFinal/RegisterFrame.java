import javax.swing.*; // นำเข้าเครื่องมือ Swing สำหรับสร้างหน้าต่าง ปุ่ม และช่องกรอกข้อมูล
import java.awt.*; // นำเข้าเครื่องมือจัดสี แบบอักษร ขนาด ตำแหน่ง และการวาดภาพ
import java.util.List; // นำเข้า List เพื่อเก็บข้อมูลผู้ใช้หลายคนเป็นรายการ

public class RegisterFrame extends JFrame { // สร้างคลาสหน้าสมัครสมาชิก โดยสืบทอดความสามารถของหน้าต่าง JFrame

    // ── 1. ตัวแปรข้อมูลและช่องกรอก ──
    private CSVManager csvManager; // ประกาศตัวแปรเก็บตัวช่วยอ่านและเขียนข้อมูลในไฟล์ CSV
    private JTextField usernameField; // ประกาศตัวแปรสำหรับช่องกรอกชื่อผู้ใช้
    private JPasswordField passwordField; // ประกาศตัวแปรสำหรับช่องกรอกรหัสผ่านที่ซ่อนตัวอักษรได้
    private JPasswordField confirmPasswordField; // ประกาศตัวแปรสำหรับช่องกรอกรหัสผ่านซ้ำเพื่อยืนยัน

    // ── 2. สร้างหน้าต่างและกำหนดการทำงานของปุ่ม ──
    public RegisterFrame() { // เริ่มคำสั่งเตรียมหน้าสมัครสมาชิกเมื่อสร้าง RegisterFrame ขึ้นมา
        super("PhotoHunt - Sign up"); // ตั้งชื่อบนแถบหน้าต่างเป็น PhotoHunt - Sign up
        csvManager = new CSVManager(); // สร้างตัวช่วยจัดการข้อมูลผู้ใช้ในไฟล์ CSV
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // กำหนดให้โปรแกรมจบการทำงานเมื่อกดปิดหน้าต่างนี้

        usernameField = new HintTextField("username"); // สร้างช่องชื่อจากโค้ดภายในไฟล์นี้
        passwordField = new HintPasswordField("password"); // สร้างช่องรหัสพร้อมปุ่มรูปตาภายในไฟล์นี้
        confirmPasswordField = new HintPasswordField("confirm password"); // สร้างช่องยืนยันรหัสพร้อมปุ่มรูปตา
        JButton loginButton = new JButton("login"); // สร้างปุ่ม login สำหรับกลับไปหน้าล็อกอิน
        JButton registerButton = new JButton("sign up"); // สร้างปุ่มที่มีข้อความ sign up
        setContentPane(createRegisterPanel(usernameField, passwordField, confirmPasswordField, registerButton, loginButton)); // ใช้ UI สมัครสมาชิกที่สร้างในไฟล์นี้
        getRootPane().setDefaultButton(registerButton); // กำหนดปุ่มสมัครสมาชิกเป็นปุ่มหลักสำหรับการกด Enter ในฟอร์ม

        registerButton.addActionListener(e -> { // เริ่มชุดคำสั่งที่จะทำเมื่อผู้ใช้กดปุ่มสมัครสมาชิก
            String password = new String(passwordField.getPassword()); // อ่านรหัสผ่านจากช่องกรอกแล้วแปลงจากชุดตัวอักษรเป็นข้อความ
            String confirmation = new String(confirmPasswordField.getPassword()); // อ่านรหัสผ่านที่กรอกซ้ำในช่องยืนยันแล้วแปลงเป็นข้อความ
            if (!password.equals(confirmation)) { // ตรวจว่ารหัสผ่านกับรหัสยืนยันไม่ตรงกันหรือไม่
                JOptionPane.showMessageDialog(this, "password do not match", // เตรียมแสดงข้อความเตือนว่ารหัสผ่านทั้งสองช่องไม่ตรงกัน
                        "Sign up", JOptionPane.WARNING_MESSAGE); // ตั้งชื่อกล่องข้อความเป็น Sign up และแสดงสัญลักษณ์คำเตือน
                return; // หยุดคำสั่งในเหตุการณ์นี้ทันที โดยไม่ทำบรรทัดถัดไป
            } // จบส่วนคำสั่งนี้
            try { // เริ่มส่วนที่อาจเกิดข้อผิดพลาด เพื่อให้ catch รับไปจัดการได้
                if (register(usernameField.getText(), password)) { // ลองสมัครด้วยชื่อและรหัสที่กรอก แล้วทำส่วนนี้เมื่อบันทึกสำเร็จ
                    JOptionPane.showMessageDialog(this, "Registration successful. Please log in."); // แจ้งว่าสมัครสมาชิกสำเร็จแล้ว และให้เข้าสู่ระบบ
                    openLogin(); // เรียกเมธอดเพื่อกลับไปหน้าล็อกอิน
                } // จบกรณีสมัครสำเร็จ
            } catch (IllegalArgumentException ex) { // รับคำเตือนข้อมูลไม่ถูกต้อง แสดงเพียงครั้งเดียว
                JOptionPane.showMessageDialog(this, ex.getMessage(), // แสดงสาเหตุที่สมัครไม่ได้
                        "Sign up", JOptionPane.WARNING_MESSAGE); // ใช้กล่องคำเตือนสมัครสมาชิก
            } catch (RuntimeException ex) { // รับข้อผิดพลาดระหว่างทำงานจากส่วน try เพื่อแจ้งผู้ใช้
                JOptionPane.showMessageDialog(this, // เริ่มสร้างกล่องข้อความแจ้งผู้ใช้บนหน้าต่างนี้
                        "Could not save the account. Please check CSVManager and the user data file.", // กำหนดข้อความว่าบันทึกบัญชีไม่ได้ ให้ตรวจตัวจัดการ CSV และไฟล์ข้อมูลผู้ใช้
                        "Data error", JOptionPane.ERROR_MESSAGE); // ตั้งหัวข้อว่า Data error และแสดงสัญลักษณ์ข้อผิดพลาด
            } // จบส่วนคำสั่งนี้
        }); // จบชุดคำสั่งและการเรียกเมธอด
        loginButton.addActionListener(e -> openLogin()); // เมื่อกด login ให้กลับไปหน้าล็อกอิน
        setSize(1174, 863); // กำหนดขนาดหน้าจอ
        setMinimumSize(new Dimension(440, 560)); // กำหนดขนาดเล็กสุดของหน้าต่าง
        setLocationRelativeTo(null); // วางหน้าต่างไว้กลางหน้าจอ
    } // จบส่วนคำสั่งนี้

    // ── 3. ตรวจข้อมูลและบันทึกบัญชี ──
    public boolean register(String u, String p) { // รับชื่อและรหัสผ่านจากหน้าสมัคร
        return registerAccount(csvManager, u, p); // ตรวจและบันทึกผ่านเมธอดด้านล่าง
    } // จบเมธอดสมัครของหน้าต่าง

    // ตรวจและบันทึกในจุดเดียว เพื่อไม่ให้ข้อมูลที่ไม่ผ่านถูกเขียนลง CSV
    static boolean registerAccount(CSVManager csv, String username, String password) { // รับตัวจัดการไฟล์ ชื่อ และรหัส
        if (username == null || username.trim().isEmpty()) { // ถ้าชื่อว่าง ให้หยุดสมัคร
            throw new IllegalArgumentException("Please enter a username"); // ส่งข้อความเตือนเรื่องชื่อ
        } // จบการตรวจชื่อว่าง
        if (password == null || password.trim().isEmpty()) { // ถ้ารหัสว่าง ให้หยุดสมัคร
            throw new IllegalArgumentException("Please enter a password"); // ส่งข้อความเตือนเรื่องรหัส
        } // จบการตรวจรหัสว่าง
        if(password.length() < 8){
            throw new IllegalArgumentException("password too short");
        }
        // ── ห้ามภาษาไทย: ตรวจตัวอักษรจริงทีละตัวก่อนบันทึก ──
        for (int i = 0; i < password.length(); i++) { // วนตั้งแต่ตัวแรกจนถึงตัวสุดท้าย
            char letter = password.charAt(i); // อ่านตัวอักษรที่ตำแหน่ง i
            if (letter >= 0x0E00 && letter <= 0x0E7F) { // ช่วงรหัสอักขระไทย รวมสระ วรรณยุกต์ และเลขไทย
                throw new IllegalArgumentException("Avoid using thai alphabets"); // หยุดทันที ไม่บันทึกบัญชีนี้
            } // จบการตรวจอักขระไทย
        } // จบการวนตรวจรหัสผ่าน

        username = username.trim(); // ตัดช่องว่างหน้าและหลังชื่อ
        if (username.indexOf(',') >= 0 || username.indexOf('\r') >= 0 || username.indexOf('\n') >= 0) { // ตรวจตัวคั่นที่ทำให้ชื่อเสียรูปแบบ CSV
            throw new IllegalArgumentException("username must not have commma , and newline"); // แจ้งเฉพาะปัญหาชื่อ
        } // จบการตรวจรูปแบบชื่อ
        if (password.indexOf('\r') >= 0 || password.indexOf('\n') >= 0) { // รหัสต้องอยู่บรรทัดเดียว ส่วนสัญลักษณ์ใช้ได้
            throw new IllegalArgumentException("password must not newline"); // แจ้งเฉพาะปัญหารหัส
        } // จบการตรวจรูปแบบรหัส

        // ── ตรวจชื่อซ้ำ: แจ้งชื่อซ้ำแยกจากปัญหารหัสผ่าน ──
        List<User> users = csv.readUsers(); // อ่านบัญชีทั้งหมดจากไฟล์
        if (users == null) { // ถ้าอ่านรายการไม่ได้ ให้แจ้งข้อผิดพลาดข้อมูล
            throw new IllegalStateException("Could not read accounts."); // ส่งให้ส่วนจัดการข้อผิดพลาดไฟล์
        } // จบการตรวจรายการบัญชี
        for (User user : users) { // ตรวจบัญชีทีละคน
            if (user != null && username.equals(user.getUsername())) { // ถ้าชื่อเหมือนบัญชีที่มีอยู่แล้ว
                throw new IllegalArgumentException("username already existed please use a new username"); // หยุดสมัครและแจ้งว่าชื่อซ้ำ
            } // จบการตรวจชื่อซ้ำ
        } // จบการวนบัญชี

        // ── บันทึกเมื่อผ่านทุกเงื่อนไขเท่านั้น ──
        csv.writeUser(new User(username, password)); // บันทึกชื่อและรหัสที่ตรวจผ่านแล้ว
        List<User> savedUsers = csv.readUsers(); // อ่านกลับมาตรวจการบันทึก
        if (savedUsers != null) { // ตรวจต่อเมื่ออ่านรายการได้
            for (User user : savedUsers) { // วนหาบัญชีที่เพิ่งสมัคร
                if (user != null && username.equals(user.getUsername()) && password.equals(user.getPassword())) { // ชื่อและรหัสต้องตรงทั้งคู่
                    return true; // ยืนยันว่าบันทึกสำเร็จ
                } // จบการตรวจบัญชีที่บันทึก
            } // จบการวนรายการ
        } // จบการตรวจผลบันทึก
        throw new IllegalStateException("CSVManager did not persist the account."); // แจ้งข้อผิดพลาดถ้าไม่พบข้อมูลที่บันทึก
    } // จบเมธอดตรวจและบันทึก

    // ── 4. กลับไปหน้าล็อกอิน ──
    public void openLogin() { // สร้างเมธอดสำหรับกลับไปหน้าล็อกอิน
        new LoginFrame().setVisible(true); // สร้างและแสดงหน้าต่างล็อกอิน
        dispose(); // ปิดหน้าต่างปัจจุบันและคืนทรัพยากร
    } // จบส่วนคำสั่งนี้

    // ── 5. UI ของหน้านี้: แก้ตำแหน่ง สี และขนาดได้ที่นี่ ──
    static JPanel createRegisterPanel(JTextField username, // รับช่องชื่อของหน้านี้
            JPasswordField password, JPasswordField confirmation, // รับช่องรหัสและช่องยืนยัน
            JButton primary, JButton link) { // รับปุ่มหลักและปุ่มเปลี่ยนหน้า
        JLabel title = new JLabel("Photo hunt", SwingConstants.CENTER); // หัวเรื่องของหน้านี้
        JLabel subtitle = new JLabel("create your account", SwingConstants.CENTER); // ข้อความใต้หัวเรื่อง
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4)); // แถวข้อความและลิงก์ด้านล่าง
        JPanel panel = new JPanel(null) { // สร้างพื้นที่แบบฟอร์มที่กำหนดตำแหน่งเอง พร้อมปรับวิธีวาดและจัดวางภายใน
            @Override // ระบุว่าเมธอดถัดไปเขียนแทนเมธอดที่สืบทอดมา

            public void paint(Graphics g) { // กำหนดวิธีวาดแบบฟอร์ม โดย g คือเครื่องมือวาดที่ระบบส่งมา
                Graphics2D copy = (Graphics2D) g.create(); // สร้างสำเนาเครื่องมือวาดเพื่อปรับแต่งโดยไม่กระทบเครื่องมือเดิม
                copy.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, // เริ่มตั้งค่าการวาดขอบตัวอักษรให้ดูเรียบขึ้น
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // เปิดใช้การเกลี่ยขอบตัวอักษรให้ไม่ดูเป็นขั้นบันได
                super.paint(copy); // ให้วิธีวาดเดิมวาดแบบฟอร์มและส่วนประกอบด้วยเครื่องมือที่ตั้งค่าไว้
                copy.dispose(); // คืนทรัพยากรของเครื่องมือวาดสำเนา
            } // จบส่วนคำสั่งนี้

            @Override // ระบุว่าเมธอดถัดไปเขียนแทนเมธอดที่สืบทอดมา

            public void doLayout() { // กำหนดวิธีจัดตำแหน่งและขนาดของส่วนประกอบเมื่อระบบจัดหน้าจอ
                int width = Math.min(320, getWidth() - 64); // กำหนดความกว้างช่องกรอกไม่เกิน 320 และเว้นขอบรวมอย่างน้อย 64 px
                int x = (getWidth() - width) / 2; // คำนวณตำแหน่งซ้ายเพื่อให้ช่องกรอกอยู่กึ่งกลางแนวนอน
                int top = Math.max(24, (int) (getHeight() * 0.10)); // เริ่มหัวเรื่องที่ระยะ 10 เปอร์เซ็นต์จากด้านบน แต่ไม่น้อยกว่า 24 px
                title.setBounds(x - 30, top, width + 60, 40); // กำหนดตำแหน่งและขนาดหัวเรื่อง
                subtitle.setBounds(x - 30, top + 40, width + 60, 32); // กำหนดตำแหน่งและขนาดข้อความใต้หัวเรื่อง
                int fieldTop = top + 99; // ตำแหน่งเริ่มต้นช่องชื่อผู้ใช้ ปรับตัวเลขนี้เพื่อเลื่อนช่องกรอก
                username.setBounds(x, fieldTop, width, 45); // วางช่องชื่อผู้ใช้ตามตำแหน่งที่คำนวณและกำหนดความสูง 45 px
                password.setBounds(x, fieldTop + 65, width, 45); // วางช่องรหัสผ่านใต้ช่องชื่อผู้ใช้

                confirmation.setBounds(x, fieldTop + 130, width, 45); // วางช่องยืนยันรหัสใต้ช่องรหัสผ่าน
                int buttonTop = fieldTop + 195; // คำนวณตำแหน่งปุ่มหลักใต้ช่องกรอก
                primary.setBounds(x, buttonTop, width, 48); // วางปุ่มหลักให้กว้างเท่าช่องกรอกและสูง 48 px
                footer.setBounds( // กำหนดพื้นที่แถวลิงก์ด้านล่าง
                        x - 35, buttonTop + 70, width + 70, 32); // วางแถวลิงก์ใต้ปุ่ม โดยเว้นระยะไม่ให้ทับกัน
            } // จบส่วนคำสั่งนี้
        }; // จบการสร้างออบเจ็กต์ที่ปรับแต่งไว้

        panel.setBackground(Color.WHITE); // กำหนดพื้นหลังแบบฟอร์มเป็นสีขาว
        panel.setPreferredSize(new Dimension(440, 560)); // ขนาดแบบฟอร์มของหน้านี้ กว้าง 440 สูง 560

        title.setFont(new Font("SansSerif", Font.BOLD, // กำหนดแบบอักษร รูปแบบ และขนาดข้อความ
                26)); // กำหนดขนาดหัวเรื่อง 26
        title.setForeground(new Color(25, 25, 25)); // กำหนดสีข้อความหัวเรื่องเป็นเทาเข้มเกือบดำ
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14)); // กำหนดแบบอักษร รูปแบบ และขนาดข้อความ
        subtitle.setForeground(new Color(178, 178, 178)); // กำหนดสีคำบรรยายเป็นสีเทาอ่อน

        panel.add(title); // ใส่หัวเรื่องลงในแบบฟอร์มเป็นส่วนประกอบแรก
        panel.add(subtitle); // ใส่คำบรรยายลงในแบบฟอร์มเป็นส่วนประกอบที่สอง
        panel.add(username); // ใส่ช่องชื่อผู้ใช้ลงในแบบฟอร์ม
        panel.add(password); // ใส่ช่องรหัสผ่านลงในแบบฟอร์ม

        panel.add(confirmation); // ใส่ช่องยืนยันรหัสผ่านลงในหน้าสมัครสมาชิก

        primary.setFont(new Font("SansSerif", Font.PLAIN, 14)); // กำหนดแบบอักษร รูปแบบ และขนาดข้อความ
        primary.setForeground(Color.WHITE); // กำหนดข้อความบนปุ่มหลักเป็นสีขาว
        primary.setBackground(new Color(76, 76, 76)); // กำหนดสีพื้นปุ่มหลักเป็นสีเทาเข้ม
        primary.setUI(new javax.swing.plaf.basic.BasicButtonUI() { // กำหนดวิธีวาดปุ่มหลักเอง เพื่อทำพื้นหลังมุมโค้งและสีตามสถานะ
            @Override // ระบุว่าเมธอดถัดไปเขียนแทนเมธอดที่สืบทอดมา
            public void paint(Graphics g, JComponent c) { // เริ่มวิธีวาดปุ่ม โดย c คือส่วนประกอบปุ่มที่กำลังวาด
                Graphics2D copy = (Graphics2D) g.create(); // สร้างสำเนาเครื่องมือวาดเพื่อปรับแต่งโดยไม่กระทบเครื่องมือเดิม
                copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // เริ่มตั้งค่าการวาดเส้นและรูปทรงให้ขอบเรียบ
                        RenderingHints.VALUE_ANTIALIAS_ON); // เปิดใช้การเกลี่ยขอบรูปทรงให้ไม่ดูเป็นขั้นบันได
                ButtonModel model = ((JButton) c).getModel(); // อ่านสถานะของปุ่ม เช่น กำลังกดหรือมีเมาส์ชี้อยู่
                copy.setColor(model.isPressed() ? new Color(50, 50, 50) // เลือกสีพื้นเทาเข้มขึ้นเมื่อปุ่มกำลังถูกกด
                        : model.isRollover() ? new Color(90, 90, 90) : c.getBackground()); // ถ้าไม่ได้กด ให้ใช้สีเมื่อเมาส์ชี้ หรือใช้สีพื้นปกติเมื่อไม่ได้ชี้
                copy.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12); // ระบายพื้นปุ่มเต็มพื้นที่เป็นสี่เหลี่ยมมุมโค้ง

                if (c.isFocusOwner()) { // ตรวจว่าปุ่มนี้เป็นตำแหน่งที่กำลังรับการควบคุมจากแป้นพิมพ์หรือไม่
                    copy.setColor(new Color(170, 170, 170)); // เลือกสีเทาอ่อนสำหรับเส้นบอกตำแหน่งที่แป้นพิมพ์กำลังควบคุม
                    copy.drawRoundRect(2, 2, c.getWidth() - 5, c.getHeight() - 5, 10, 10); // วาดเส้นมุมโค้งด้านในปุ่มเพื่อบอกว่าปุ่มนี้ถูกเลือกด้วยแป้นพิมพ์
                } // จบส่วนคำสั่งนี้
                copy.dispose(); // คืนทรัพยากรของเครื่องมือวาดสำเนา
                super.paint(g, c); // ให้วิธีวาดปุ่มเดิมวาดข้อความและรายละเอียดที่เหลือต่อ
            } // จบส่วนคำสั่งนี้
        }); // จบชุดคำสั่งและการเรียกเมธอด
        primary.setOpaque(false); // ระบุว่าปุ่มไม่ได้ระบายพื้นสี่เหลี่ยมทึบแบบปกติเต็มพื้นที่
        primary.setContentAreaFilled(false); // ไม่ให้ระบบเติมพื้นปุ่มแบบเดิม เพราะวาดพื้นมุมโค้งเองแล้ว
        primary.setBorderPainted(false); // ไม่ให้ระบบวาดขอบปุ่มแบบเดิม
        primary.setFocusPainted(false); // ปิดเส้นบอกการเลือกแบบเดิม เพราะวาดเส้นบอกตำแหน่งเองแล้ว
        primary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เปลี่ยนเมาส์เป็นรูปมือเมื่อชี้ปุ่มหลัก
        panel.add(primary); // ใส่ปุ่มหลักลงในแบบฟอร์ม

        footer.setOpaque(false); // ทำพื้นแถวล่างให้โปร่งใสเพื่อเห็นสีพื้นของแบบฟอร์ม
        JLabel prompt = new JLabel("already have account?"); // สร้างข้อความคำถามข้างปุ่มเปลี่ยนหน้า
        prompt.setFont(new Font("SansSerif", Font.PLAIN, 14)); // กำหนดแบบอักษร รูปแบบ และขนาดข้อความ
        prompt.setForeground(new Color(178, 178, 178)); // กำหนดสีข้อความคำถามเป็นสีเทาอ่อน
        link.setFont(new Font("SansSerif", Font.PLAIN, 14)); // กำหนดแบบอักษร รูปแบบ และขนาดข้อความ
        link.setForeground(new Color(76, 76, 76)); // กำหนดข้อความปุ่มลิงก์เป็นสีเทาเข้ม
        link.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // เอาขอบและระยะขอบปุ่มลิงก์ออก ให้ดูเหมือนข้อความที่กดได้
        link.setContentAreaFilled(false); // ไม่ระบายพื้นหลังปุ่มลิงก์
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เปลี่ยนเมาส์เป็นรูปมือเมื่อชี้ปุ่มลิงก์
        footer.add(prompt); // ใส่ข้อความคำถามลงในแถวล่าง
        footer.add(link); // ใส่ปุ่มเปลี่ยนหน้าถัดจากข้อความคำถาม
        panel.add(footer); // ใส่แถวลิงก์เป็นส่วนประกอบสุดท้ายของแบบฟอร์ม
        return panel; // ส่งแบบฟอร์มที่สร้างเสร็จแล้วกลับไปให้หน้าต่างนำไปใช้
    } // จบส่วนคำสั่งนี้

    // ── 6. สี แบบอักษร และขอบของช่องกรอก ──
    // ── 6. ส่วนตกแต่งช่องกรอกและรูปตา ใช้เฉพาะหน้านี้ ──
    private static void styleInput(JTextField field, String hint) { // เมธอดจัดหน้าตาช่องกรอก โดยรับช่องและข้อความคำใบ้มา
        field.setFont(new Font("SansSerif", Font.PLAIN, 13)); // กำหนดแบบอักษร รูปแบบ และขนาดข้อความ
        field.setForeground(Color.BLACK); // กำหนดข้อความในช่องกรอกเป็นสีดำ
        field.setCaretColor(Color.BLACK); // กำหนดเส้นกะพริบที่บอกตำแหน่งพิมพ์เป็นสีดำ
        field.setBackground(Color.WHITE); // กำหนดสีพื้นประจำช่องกรอกเป็นสีขาว
        field.setOpaque(false); // ให้ช่องกรอกโปร่งใสเพื่อใช้พื้นหลังร่วมกับแบบฟอร์ม
        field.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14)); // เว้นช่องว่างซ้ายขวาด้านละ 14 px เพื่อไม่ให้ข้อความติดขอบ
        field.getAccessibleContext().setAccessibleName(hint); // ใช้คำใบ้เป็นชื่อช่องสำหรับโปรแกรมช่วยอ่านหน้าจอ
        field.addFocusListener(new java.awt.event.FocusAdapter() { // เตรียมรับเหตุการณ์เมื่อผู้ใช้เริ่มหรือเลิกเลือกช่องกรอกนี้
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.repaint(); } // เมื่อเลือกช่องนี้ ให้วาดใหม่เพื่อเปลี่ยนสีขอบให้เห็นว่าพร้อมพิมพ์
            @Override public void focusLost(java.awt.event.FocusEvent e) { field.repaint(); } // เมื่อเลิกเลือกช่องนี้ ให้วาดใหม่เพื่อคืนสีขอบปกติ
        }); // จบชุดคำสั่งและการเรียกเมธอด
    } // จบส่วนคำสั่งนี้

    private static void paintInputBorder(Graphics g, JTextField field) { // เมธอดวาดขอบมุมโค้งให้ช่องกรอกที่ส่งเข้ามา
        Graphics2D copy = (Graphics2D) g.create(); // สร้างสำเนาเครื่องมือวาดเพื่อปรับแต่งโดยไม่กระทบเครื่องมือเดิม
        copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // เปิดการเกลี่ยขอบเส้นเพื่อให้เส้นโค้งดูเรียบ
        copy.setColor(field.isFocusOwner() ? new Color(120, 120, 120) : new Color(213, 213, 213)); // เลือกขอบเข้มเมื่อเลือกช่องนี้ ไม่เช่นนั้นใช้ขอบอ่อน
        copy.setStroke(new BasicStroke(1f)); // กำหนดเส้นขอบหนา 1 px ให้แน่นอน
        copy.draw(new java.awt.geom.RoundRectangle2D.Double(0.5, 0.5, field.getWidth() - 1, field.getHeight() - 1, 12, 12)); // เลื่อนเส้นเข้ามาครึ่งพิกเซล เส้นจึงอยู่ในพื้นที่ช่องทั้งเส้น ไม่ถูกตัดที่ขอบบนและซ้าย
        copy.dispose(); // คืนทรัพยากรของเครื่องมือวาดสำเนา
    } // จบส่วนคำสั่งนี้

    private static void paintHint(Graphics g, JTextField field, String hint) { // เมธอดวาดคำใบ้ในช่องกรอกที่ยังไม่มีข้อความ
        if (field.getDocument().getLength() == 0) { // ตรวจว่าช่องกรอกยังไม่มีตัวอักษรอยู่เลยหรือไม่
            Graphics2D copy = (Graphics2D) g.create(); // สร้างสำเนาเครื่องมือวาดเพื่อปรับแต่งโดยไม่กระทบเครื่องมือเดิม
            copy.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, // เริ่มตั้งค่าการวาดขอบตัวอักษรให้ดูเรียบขึ้น
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // เปิดใช้การเกลี่ยขอบตัวอักษรให้ไม่ดูเป็นขั้นบันได
            copy.setColor(Color.BLACK); // กำหนดสีคำใบ้เป็นสีดำ
            copy.setFont(field.getFont()); // กำหนดแบบอักษร รูปแบบ และขนาดข้อความ
            FontMetrics metrics = copy.getFontMetrics(); // อ่านขนาดตัวอักษรเพื่อช่วยวางคำใบ้ให้ตรงกลางแนวตั้ง
            copy.drawString(hint, 14, // เริ่มวาดคำใบ้ โดยเว้นจากขอบซ้าย 14 px
                    (field.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent()); // คำนวณแนวฐานตัวอักษรให้คำใบ้อยู่กึ่งกลางความสูงของช่อง
            copy.dispose(); // คืนทรัพยากรของเครื่องมือวาดสำเนา
        } // จบส่วนคำสั่งนี้
    } // จบส่วนคำสั่งนี้

    // ── ช่องชื่อ: ช่องชื่อผู้ใช้พร้อมข้อความคำใบ้ ──
    static class HintTextField extends JTextField { // สร้างชนิดช่องข้อความที่สามารถแสดงคำใบ้ตอนว่างได้
        private final String hint; // เก็บคำใบ้ กำหนดค่าแล้วเปลี่ยนไม่ได้
        HintTextField(String hint) { // เริ่มสร้างช่องข้อความพร้อมรับข้อความคำใบ้
            this.hint = hint; // เก็บคำใบ้ที่รับมาไว้ใช้วาดในช่องนี้
            styleInput(this, hint); // จัดสี แบบอักษร ระยะขอบ และชื่อสำหรับเครื่องมือช่วยอ่านให้ช่องนี้
        } // จบส่วนคำสั่งนี้
        @Override protected void paintComponent(Graphics g) { // กำหนดวิธีวาดเนื้อหาของช่องกรอกนี้แทนวิธีเดิม
            super.paintComponent(g); // ให้ช่องกรอกแบบเดิมวาดข้อความและรายละเอียดพื้นฐานก่อน
            paintHint(g, this, hint); // วาดคำใบ้เพิ่ม หากช่องนี้ยังไม่มีข้อความ
        } // จบส่วนคำสั่งนี้
        @Override protected void paintBorder(Graphics g) { paintInputBorder(g, this); } // เปลี่ยนวิธีวาดขอบช่องนี้ให้ใช้ขอบมุมโค้งที่สร้างไว้
    } // จบส่วนคำสั่งนี้

    // ── รูปตา: วาดรูปตาเปิดและรูปตาขีดทับ ──
    private static class EyeIcon implements Icon { // สร้างชนิดไอคอนรูปตาภายในไฟล์นี้ โดยทำตามข้อกำหนดของ Icon
        private final boolean visible; // เก็บสถานะแสดงรหัส true หรือซ่อนรหัส false
        EyeIcon(boolean visible) { this.visible = visible; } // รับสถานะการแสดงรหัสผ่านแล้วเก็บไว้กำหนดหน้าตารูปตา
        @Override public int getIconWidth() { return 24; } // แจ้งระบบว่าไอคอนกว้าง 24 px
        @Override public int getIconHeight() { return 24; } // แจ้งระบบว่าไอคอนสูง 24 px
        @Override public void paintIcon(Component c, Graphics g, int x, int y) { // เริ่มวาดไอคอนบนส่วนประกอบ c ที่ตำแหน่ง x และ y ที่ระบบกำหนด
            Graphics2D copy = (Graphics2D) g.create(); // สร้างสำเนาเครื่องมือวาดเพื่อปรับแต่งโดยไม่กระทบเครื่องมือเดิม
            copy.translate(x, y); // เลื่อนจุดเริ่มวาดไปยังตำแหน่งไอคอน เพื่อใช้พิกัดภายในรูปได้ง่าย
            copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // เริ่มตั้งค่าการวาดเส้นและรูปทรงให้ขอบเรียบ
                    RenderingHints.VALUE_ANTIALIAS_ON); // เปิดใช้การเกลี่ยขอบรูปทรงให้ไม่ดูเป็นขั้นบันได
            copy.setColor(new Color(100, 100, 100)); // เลือกสีเทาเข้มสำหรับวาดรูปตาและเส้นขีดทับ
            copy.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, // ตั้งความหนาเส้นรอบตาเป็น 1.8 px และทำปลายเส้นให้มน
                    BasicStroke.JOIN_ROUND)); // กำหนดจุดที่เส้นต่อกันให้โค้งมน

            java.awt.geom.Path2D eye = new java.awt.geom.Path2D.Double(); // สร้างเส้นทางสำหรับวาดรูปทรงขอบดวงตา
            eye.moveTo(2, 12); // เริ่มเส้นขอบตาที่พิกัดซ้าย 2 และบน 12 ภายในไอคอน
            eye.curveTo(7, 3, 17, 3, 22, 12); // ลากเส้นโค้งด้านบนของตาไปถึงด้านขวา โดยใช้สองจุดควบคุมความโค้ง
            eye.curveTo(17, 21, 7, 21, 2, 12); // ลากเส้นโค้งด้านล่างของตากลับมาจุดเริ่มต้นให้เป็นรูปตาครบวง
            copy.draw(eye); // วาดเส้นขอบรูปตาตามเส้นทางที่เตรียมไว้
            copy.fillOval(8, 8, 8, 8); // วาดวงกลมทึบขนาด 8 คูณ 8 pxเป็นลูกตาตรงกลาง

            if (!visible) { // เพิ่มเส้นขีดทับรูปตาเมื่อสถานะคือซ่อนรหัสผ่าน
                copy.setColor(c.getBackground()); // เลือกสีพื้นหลังของปุ่มเพื่อทำช่องว่างใต้เส้นขีดทับ
                copy.setStroke(new BasicStroke(4.5f)); // ใช้เส้นหนา 4.5 pxสำหรับลบแนวขอบตาที่เส้นขีดจะพาดผ่าน
                copy.drawLine(3, 3, 21, 21); // วาดเส้นทแยงจากพิกัด (3,3) ไป (21,21)
                copy.setColor(new Color(100, 100, 100)); // เลือกสีเทาเข้มสำหรับวาดรูปตาและเส้นขีดทับ
                copy.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, // ตั้งเส้นขีดทับสีเข้มให้หนา 2.2 pxและมีปลายมน
                        BasicStroke.JOIN_ROUND)); // กำหนดจุดที่เส้นต่อกันให้โค้งมน
                copy.drawLine(3, 3, 21, 21); // วาดเส้นทแยงจากพิกัด (3,3) ไป (21,21)
            } // จบส่วนคำสั่งนี้
            copy.dispose(); // คืนทรัพยากรของเครื่องมือวาดสำเนา
        } // จบส่วนคำสั่งนี้
    } // จบส่วนคำสั่งนี้

    // ── ช่องรหัส: ช่องรหัสผ่านและปุ่มแสดงหรือซ่อนรหัส ──
    static class HintPasswordField extends JPasswordField { // สร้างชนิดช่องรหัสผ่านที่มีทั้งคำใบ้และปุ่มรูปตา
        private final String hint; // เก็บคำใบ้ กำหนดค่าแล้วเปลี่ยนไม่ได้
        HintPasswordField(String hint) { // เริ่มสร้างช่องรหัสผ่านพร้อมรับข้อความคำใบ้
            this.hint = hint; // เก็บคำใบ้ที่รับมาไว้ใช้วาดในช่องนี้
            styleInput(this, hint); // จัดสี แบบอักษร ระยะขอบ และชื่อสำหรับเครื่องมือช่วยอ่านให้ช่องนี้
            setEchoChar('\u2022'); // ตั้งให้แสดงจุดกลมแทนตัวอักษรรหัสผ่านเมื่อเริ่มต้น

            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 48)); // เว้นซ้าย 14 และขวา 48 px เพื่อไม่ให้ข้อความรหัสทับปุ่มรูปตา
            setLayout(null); // จัดตำแหน่งปุ่มรูปตาเองโดยไม่ใช้ตัวจัดวางอัตโนมัติ

            JToggleButton eyeButton = new JToggleButton(new EyeIcon(false)); // สร้างปุ่มสลับสองสถานะ โดยเริ่มด้วยรูปตาขีดทับสำหรับรหัสที่ซ่อนอยู่
            eyeButton.setSelectedIcon(new EyeIcon(true)); // ให้ใช้รูปตาเปิดเมื่อปุ่มอยู่สถานะแสดงรหัสผ่าน
            eyeButton.setBorder(BorderFactory.createEmptyBorder()); // เอาขอบปุ่มรูปตาออกเพื่อให้กลืนกับช่องกรอก
            eyeButton.setContentAreaFilled(false); // ไม่เติมสีพื้นปุ่มรูปตา
            eyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เปลี่ยนเมาส์เป็นรูปมือเมื่อชี้ปุ่มรูปตา
            eyeButton.setToolTipText("Show password"); // ให้แสดงคำแนะนำว่าแสดงรหัสผ่านเมื่อชี้เมาส์ค้างไว้
            eyeButton.getAccessibleContext().setAccessibleName("Show password"); // ตั้งชื่อปุ่มให้โปรแกรมช่วยอ่านหน้าจอรู้ว่าใช้แสดงรหัสผ่าน

            eyeButton.addActionListener(e -> { // เริ่มชุดคำสั่งที่จะทำเมื่อกดปุ่มรูปตา
                int caret = getCaretPosition(); // จำตำแหน่งเส้นกะพริบในรหัสผ่านไว้ก่อนสลับการแสดง
                boolean visible = eyeButton.isSelected(); // อ่านสถานะปุ่ม โดย true หมายถึงผู้ใช้เลือกให้แสดงรหัสผ่าน
                setEchoChar(visible ? (char) 0 : '\u2022'); // ถ้า visible เป็น true แสดงรหัสจริง ไม่เช่นนั้นแสดงจุด
                String action = visible ? "Hide password" : "Show password"; // เลือกคำอธิบายสิ่งที่จะเกิดเมื่อกดครั้งถัดไป คือซ่อนหรือแสดงรหัสผ่าน
                eyeButton.setToolTipText(action); // ปรับคำแนะนำตอนชี้เมาส์ให้ตรงกับการกดครั้งถัดไป
                eyeButton.getAccessibleContext().setAccessibleName(action); // ปรับชื่อที่โปรแกรมช่วยอ่านหน้าจอใช้ให้ตรงกับการกดครั้งถัดไป
                requestFocusInWindow(); // ขอให้ช่องรหัสผ่านกลับมารับการพิมพ์ต่อหลังจากกดรูปตา
                setCaretPosition(caret); // คืนตำแหน่งเส้นกะพริบให้ตรงกับก่อนกดรูปตา
            }); // จบชุดคำสั่งและการเรียกเมธอด
            add(eyeButton); // ใส่ปุ่มรูปตาเป็นส่วนประกอบภายในช่องรหัสผ่าน
        } // จบส่วนคำสั่งนี้

        @Override public void doLayout() { // กำหนดวิธีจัดตำแหน่งปุ่มรูปตาภายในช่องรหัสผ่าน
            getComponent(0).setBounds(Math.max(0, getWidth() - 44), // วางปุ่มรูปตาซึ่งเป็นส่วนประกอบแรกไว้ชิดขวา โดยไม่ให้ตำแหน่งซ้ายติดลบ
                    Math.max(0, (getHeight() - 36) / 2), 40, 36); // จัดปุ่มกึ่งกลางแนวตั้ง และให้ปุ่มกว้าง 40 สูง 36 px
        } // จบส่วนคำสั่งนี้
        @Override protected void paintComponent(Graphics g) { // กำหนดวิธีวาดเนื้อหาของช่องกรอกนี้แทนวิธีเดิม
            super.paintComponent(g); // ให้ช่องกรอกแบบเดิมวาดข้อความและรายละเอียดพื้นฐานก่อน
            paintHint(g, this, hint); // วาดคำใบ้เพิ่ม หากช่องนี้ยังไม่มีข้อความ
        } // จบส่วนคำสั่งนี้
        @Override protected void paintBorder(Graphics g) { paintInputBorder(g, this); } // เปลี่ยนวิธีวาดขอบช่องนี้ให้ใช้ขอบมุมโค้งที่สร้างไว้
    } // จบส่วนคำสั่งนี้
} // จบคลาส RegisterFrame