import javax.swing.*; // นำเข้าเครื่องมือสร้างหน้าต่างโปรแกรม (UI)
import java.awt.*; // นำเข้าเครื่องมือจัดการกราฟิก สี และขนาด
import java.util.List; // นำเข้าเครื่องมือจัดการชุดข้อมูลแบบ List

// Owner: นายวชิรวิทย์ กตกุลบัญชร (Authentication)
public class LoginFrame extends JFrame { // สร้างคลาส LoginFrame เป็นหน้าต่างโปรแกรม

    private CSVManager csvManager; // ตัวจัดการไฟล์ CSV สำหรับอ่านข้อมูล
    private JTextField usernameField; // ช่องสำหรับกรอกชื่อผู้ใช้
    private JPasswordField passwordField; // ช่องสำหรับกรอกรหัสผ่าน

    public LoginFrame() { // Constructor เริ่มต้นทำงานเมื่อเปิดหน้าต่าง
        super("PhotoHunt - Login"); // ตั้งชื่อหัวหน้าต่าง
        csvManager = new CSVManager(); // สร้างตัวอ่านไฟล์ CSV
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ปิดโปรแกรมเมื่อกดกากบาท

        usernameField = new LoginFrame.HintTextField("username"); // สร้างช่องกรอกชื่อพร้อมคำใบ้ลางๆ
        passwordField = new LoginFrame.HintPasswordField("password"); // สร้างช่องกรอกรหัสพร้อมคำใบ้ลางๆ
        JButton loginButton = new JButton("Login"); // สร้างปุ่ม Login
        JButton registerButton = new JButton("sign up"); // สร้างปุ่ม sign up
        JButton guestButton = new JButton("Play as Guest"); // สร้างปุ่มเล่นแบบ Guest
        setContentPane(LoginFrame.createAuthPanel(false, usernameField,
                passwordField, null,
                loginButton, registerButton,guestButton)); // นำของทั้งหมดไปจัดเรียงในแผงหน้าจอ
        getRootPane().setDefaultButton(loginButton); // ตั้งปุ่ม Login เป็นปุ่มหลัก (กด Enter เพื่อทำงานได้)

        loginButton.addActionListener(e -> { // ตั้งค่าเมื่อปุ่ม Login ถูกคลิก
            String username = usernameField.getText(); // ดึงข้อความชื่อผู้ใช้
            String password = new String(passwordField.getPassword()); // ดึงข้อความรหัสผ่าน
            if (username.trim().isEmpty() || password.trim().isEmpty()) { // เช็คว่าช่องว่างเปล่าหรือไม่
                JOptionPane.showMessageDialog(this, "Please enter a username and password.",
                        "Login", JOptionPane.WARNING_MESSAGE); // แจ้งเตือนให้กรอกข้อมูล
                return; // จบการทำงาน ไม่ไปต่อ
            }
            try { // ลองเช็คข้อมูลผู้ใช้
                User user = login(username, password); // เรียกฟังก์ชันเช็คการเข้าสู่ระบบ
                if (user != null) { // ถ้าพบผู้ใช้และรหัสถูกต้อง
                    passwordField.setText(""); // ล้างช่องรหัสผ่าน
                    openMain(user); // เปิดหน้าจอหลัก
                } else { // ถ้ารหัสผิดหรือไม่พบผู้ใช้
                    JOptionPane.showMessageDialog(this, "Incorrect username or password.",
                            "Login", JOptionPane.WARNING_MESSAGE); // แจ้งเตือนรหัสผิด
                    passwordField.setText(""); // ล้างช่องรหัสผ่านเตรียมให้พิมพ์ใหม่
                    passwordField.requestFocusInWindow(); // โฟกัสเคอร์เซอร์ไปที่ช่องรหัสผ่าน
                }
            } catch (RuntimeException ex) { // ถ้าเกิดข้อผิดพลาดในการอ่านไฟล์
                JOptionPane.showMessageDialog(this,
                        "Could not read accounts. Please check CSVManager and the user data file.",
                        "Data error", JOptionPane.ERROR_MESSAGE); // แจ้งเตือนว่าไฟล์มีปัญหา
            }
        });
        registerButton.addActionListener(e -> openRegister()); // ตั้งค่าปุ่มลงทะเบียนให้ไปเปิดหน้าสมัครสมาชิก

        guestButton.addActionListener(e -> { // ตั้งค่าปุ่ม Guest
            User guestUser = new User(true); // สร้างผู้ใช้แบบ Guest
            openMain(guestUser); // เปิดหน้าจอหลักแบบ Guest
        });

        
        pack(); // จัดขนาดหน้าต่างให้พอดีเนื้อหา
        setMinimumSize(new Dimension(560, 640)); // ตั้งขนาดหน้าต่างขั้นต่ำ
        setLocationRelativeTo(null); // จัดหน้าต่างให้อยู่กึ่งกลางหน้าจอคอมพิวเตอร์
    }

    // ตรวจทั้งชื่อและรหัสผ่านให้ตรงกับบัญชีเดียวกัน
    public User login(String u, String p) { // ฟังก์ชันตรวจการเข้าสู่ระบบ
        if (u == null || p == null || u.trim().isEmpty() || p.trim().isEmpty()) { // ถ้าข้อมูลว่างเปล่า
            return null; // คืนค่าความว่างเปล่า
        }
        String username = u.trim(); // ตัดช่องว่างส่วนเกินออกจากชื่อ
        List<User> users = csvManager.readUsers(); // อ่านรายชื่อผู้ใช้ทั้งหมดจากไฟล์ CSV
        if (users == null) { // ถ้าอ่านไม่ได้
            throw new IllegalStateException("CSVManager.readUsers() must return a list, not null."); // โยน error แจ้งระบบ
        }
        for (User user : users) { // วนลูปตรวจสอบรายชื่อทีละคน
            if (user != null && username.equals(user.getUsername()) && p.equals(user.getPassword())) { // ถ้าชื่อตรงและรหัสตรง
                return user; // คืนค่าข้อมูลบัญชีนั้นๆ
            }
        }
        return null; // ถ้าไม่ตรงเลยคืนค่าว่างเปล่า
    }

    public void openRegister() { // ฟังก์ชันเปิดหน้าสมัครสมาชิก
        new RegisterFrame().setVisible(true); // สร้างและแสดงหน้าต่างลงทะเบียน
        dispose(); // ปิดหน้าต่างล็อกอินปัจจุบันทิ้ง
    }

    public void openMain(User user) { // ฟังก์ชันเปิดหน้าจอหลัก
        if (user == null) { // ถ้าข้อมูลผู้ใช้ไม่มี
            throw new IllegalArgumentException("A logged-in user is required."); // โยน error
        }
        new MainFrame(user).setVisible(true); // สร้างและแสดงหน้าจอหลักพร้อมส่งผู้ใช้เข้าไป
        dispose(); // ปิดหน้าต่างล็อกอินทิ้ง
    }

    // ใช้หน้าตาเดียวกันทั้งสองหน้า โดยไม่ต้องเพิ่มไฟล์ UI แยก
    static JPanel createAuthPanel(boolean signUp, JTextField username, // ฟังก์ชันสร้างแผงหน้าจอ UI
            JPasswordField password, JPasswordField confirmation, // รับช่องกรอกข้อมูลต่างๆ
            JButton primary, JButton link,JButton guestBTn) { // รับปุ่มต่างๆ เข้ามาจัดวาง
        JPanel panel = new JPanel(null) { // สร้างแผงแบบกำหนดตำแหน่งเอง (null layout)
            @Override
            public void paint(Graphics g) { // ปรับแต่งการวาดกราฟิก
                Graphics2D copy = (Graphics2D) g.create(); // สร้างตัววาดกราฟิก 2 มิติ
                copy.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, // เปิดโหมดลบรอยหยัก
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // ทำให้ตัวหนังสือเนียนสวยขึ้น
                super.paint(copy); // วาดภาพ
                copy.dispose(); // คืนหน่วยความจำกราฟิก
            }

            @Override
            public void doLayout() { // ฟังก์ชันกำหนดตำแหน่งและขนาดของแต่ละชิ้นส่วน
                int width = Math.min(368, getWidth() - 64); // คำนวณความกว้างช่องกรอกให้พอดี
                int x = (getWidth() - width) / 2; // คำนวณจุดกึ่งกลางหน้าจอในแนวแกน X
                int top = Math.max(24, (int) (getHeight() * 0.20)); // คำนวณจุดเริ่มต้นแกน Y
                getComponent(0).setBounds(x - 30, top, width + 60, 40); // จัดตำแหน่งหัวข้อ title
                getComponent(1).setBounds(x - 30, top + 40, width + 60, 32); // จัดตำแหน่งหัวข้อย่อย subtitle
                int fieldTop = top + (signUp ? 100 : 132); // คำนวณจุดเริ่มวางช่องกรอก (ต่างกันถ้าเป็นหน้าสมัคร)
                username.setBounds(x, fieldTop, width, 45); // จัดตำแหน่งช่องชื่อ
                password.setBounds(x, fieldTop + (signUp ? 81 : 76), width, 45); // จัดตำแหน่งช่องรหัส
                if (signUp) { // ถ้าเป็นหน้าสมัครสมาชิก
                    confirmation.setBounds(x, fieldTop + 162, width, 45); // วางช่องยืนยันรหัสผ่านด้วย
                }
                int buttonTop = fieldTop + (signUp ? 233 : 149); // คำนวณจุดเริ่มวางปุ่มกด
                primary.setBounds(x, buttonTop, width, 57); // จัดตำแหน่งปุ่มหลัก (Login/Register)
                getComponent(getComponentCount() - 1).setBounds( // จัดตำแหน่งส่วนท้าย (link สลับหน้า)
                        x - 35, buttonTop + 76, width + 70, 32);

                if(guestBTn != null){ // ถ้ามีการส่งปุ่ม Guest มา
                    guestBTn.setBounds(x,buttonTop +58 ,width , 45); // จัดตำแหน่งปุ่ม Guest
                }

                getComponent(getComponentCount()- 1 ).setBounds( // จัดตำแหน่งส่วนท้ายใหม่ถ้ามีปุ่ม Guest มาคั่น
                    x - 35 ,buttonTop + (guestBTn != null ? 108: 76),width + 70,32);
            }
        };
        panel.setBackground(Color.WHITE); // ตั้งพื้นหลังแผงเป็นสีขาว
        panel.setPreferredSize(new Dimension(1174, 836)); // ตั้งขนาดความกว้างยาวแนะนำ

        JLabel title = new JLabel("Photo hunt", SwingConstants.CENTER); // สร้างข้อความหัวเรื่อง "Photo hunt" ตรงกลาง
        title.setFont(new Font("SansSerif", signUp ? Font.BOLD : Font.PLAIN, // เลือกฟอนต์และปรับความหนา
                signUp ? 26 : 30)); // ปรับขนาดฟอนต์
        title.setForeground(new Color(25, 25, 25)); // ตั้งสีตัวหนังสือให้ดำเทา
        JLabel subtitle = new JLabel(signUp ? "create your account" : "welcome back", // สร้างคำอธิบายใต้หัวเรื่อง
                SwingConstants.CENTER); // จัดให้อยู่ตรงกลาง
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, signUp ? 16 : 30)); // ปรับขนาดฟอนต์
        subtitle.setForeground(new Color(178, 178, 178)); // ตั้งสีให้เทาจางๆ
        panel.add(title); // เอาข้อความใส่ในแผง
        panel.add(subtitle); // เอาข้อความย่อยใส่ในแผง
        panel.add(username); // เอาช่องกรอกชื่อใส่ในแผง
        panel.add(password); // เอาช่องกรอกรหัสใส่ในแผง
        if (signUp) { // ถ้าเป็นโหมดสมัครสมาชิก
            panel.add(confirmation); // เพิ่มช่องยืนยันรหัสเข้าแผงด้วย
        }
        primary.setFont(new Font("SansSerif", Font.PLAIN, signUp ? 16 : 21)); // ปรับฟอนต์ปุ่มกดหลัก
        primary.setForeground(Color.WHITE); // ให้ตัวหนังสือในปุ่มเป็นสีขาว
        primary.setBackground(new Color(76, 76, 76)); // ให้พื้นหลังปุ่มเป็นสีเทาเข้ม
        primary.setUI(new javax.swing.plaf.basic.BasicButtonUI() { // สร้างการวาดปุ่มกดขึ้นมาใหม่เองเพื่อความสวยงาม
            @Override
            public void paint(Graphics g, JComponent c) { // เขียนฟังก์ชันวาดปุ่ม
                Graphics2D copy = (Graphics2D) g.create(); // สร้างตัววาด 2 มิติ
                copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // เปิดระบบลบรอยหยักให้ปุ่มไม่เป็นฟันปลา
                        RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel model = ((JButton) c).getModel(); // ตรวจสอบสถานะของปุ่มว่ากำลังโดนชี้หรือโดนกด
                copy.setColor(model.isPressed() ? new Color(50, 50, 50) // ถ้าโดนกดอยู่ ให้สีเข้มขึ้น (50,50,50)
                        : model.isRollover() ? new Color(90, 90, 90) : c.getBackground()); // ถ้าเมาส์ชี้อยู่สีสว่างขึ้น ถ้านิ่งๆ ใช้สีเดิม
                copy.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12); // วาดปุ่มเป็นรูปสี่เหลี่ยมขอบมน (มุมโค้ง 12)
                if (c.isFocusOwner()) { // ถ้าปุ่มนี้กำลังถูกเลือก (โฟกัสด้วยคีย์บอร์ด)
                    copy.setColor(new Color(170, 170, 170)); // เปลี่ยนสีเส้นขอบเป็นสีเทาสว่าง
                    copy.drawRoundRect(2, 2, c.getWidth() - 5, c.getHeight() - 5, 10, 10); // วาดเส้นขอบด้านใน
                }
                copy.dispose(); // คืนหน่วยความจำกราฟิก
                super.paint(g, c); // สั่งวาดตัวอักษรในปุ่มต่อ
            }
        });
        primary.setOpaque(false); // ปิดฉากหลังแบบเดิม
        primary.setContentAreaFilled(false); // ปิดการเติมสีปุ่มแบบเดิม
        primary.setBorderPainted(false); // ปิดเส้นขอบแบบเดิม
        primary.setFocusPainted(false); // ปิดกรอบเส้นประเวลาปุ่มโดนโฟกัส
        primary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เปลี่ยนเมาส์เป็นรูปมือเวลาชี้
        panel.add(primary); // เพิ่มปุ่มลงในแผง

        if(guestBTn != null){ // ถ้ามีปุ่มเล่นแบบ Guest ส่งมา
            guestBTn.setFont(new Font("SansSerif", Font.PLAIN, 16)); // ปรับฟอนต์
            guestBTn.setForeground(new Color(76, 76, 76)); // ตั้งสีตัวอักษร
            guestBTn.setBackground(new Color(235, 235, 235)); // ตั้งสีพื้นหลังเป็นเทาอ่อนๆ
            guestBTn.setFocusPainted(false); // ปิดขอบโฟกัส
            guestBTn.setBorderPainted(false); // ปิดเส้นขอบ
            guestBTn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เปลี่ยนเมาส์เป็นรูปมือ
            panel.add(guestBTn); // เพิ่มปุ่มลงแผง
        }
        

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4)); // สร้างแผงด้านล่างสำหรับคำถาม "มีบัญชีหรือยัง?"
        footer.setOpaque(false); // ทำให้แผงโปร่งใสเห็นพื้นหลัง
        JLabel prompt = new JLabel(signUp ? "already have account?" : "dont have any account?"); // สร้างข้อความตามโหมด
        prompt.setFont(new Font("SansSerif", Font.PLAIN, 16)); // ตั้งค่าฟอนต์
        prompt.setForeground(new Color(178, 178, 178)); // ตั้งสีตัวหนังสือ
        link.setFont(new Font("SansSerif", Font.PLAIN, 16)); // ตั้งค่าฟอนต์สำหรับลิงก์กดสลับหน้า
        link.setForeground(new Color(76, 76, 76)); // ตั้งสีตัวหนังสือให้เข้มกว่า
        link.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // ลบเส้นขอบปุ่ม
        link.setContentAreaFilled(false); // ลบพื้นหลังปุ่มเพื่อให้ดูเหมือนข้อความธรรมดา
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เปลี่ยนเคอร์เซอร์เป็นรูปมือ
        footer.add(prompt); // เพิ่มข้อความลงแผงล่าง
        footer.add(link); // เพิ่มปุ่มลิงก์ลงแผงล่าง
        panel.add(footer); // เอาแผงล่างสุด ไปใส่ในแผงใหญ่
        return panel; // ส่งออกแผงที่จัดเตรียมเสร็จแล้วเพื่อนำไปแสดงผล
    }

    private static void styleInput(JTextField field, String hint) { // ฟังก์ชันตั้งค่าหน้าตาช่องกรอกข้อมูลพื้นฐาน
        field.setFont(new Font("SansSerif", Font.PLAIN, 13)); // ตั้งฟอนต์
        field.setForeground(Color.BLACK); // ตั้งสีตัวอักษรเป็นสีดำ
        field.setCaretColor(Color.BLACK); // ตั้งสีเคอร์เซอร์กะพริบเป็นสีดำ
        field.setBackground(Color.WHITE); // ตั้งพื้นหลังสีขาว
        field.setOpaque(false); // ให้พื้นหลังช่องโปร่งใสเพื่อให้เห็นขอบมนชัดเจน
        field.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14)); // เว้นช่องว่างขอบซ้ายขวาด้านใน เพื่อให้พิมพ์แล้วไม่ชิดขอบไป
        field.getAccessibleContext().setAccessibleName(hint); // ตั้งค่าชื่อช่องสำหรับระบบผู้พิการ (Accessibility)
        field.addFocusListener(new java.awt.event.FocusAdapter() { // เพิ่มตัวจับเหตุการณ์เวลามีการคลิกเลือกช่องนี้
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.repaint(); } // สั่งให้วาดกรอบใหม่ตอนคลิกเข้า
            @Override public void focusLost(java.awt.event.FocusEvent e) { field.repaint(); } // สั่งให้วาดกรอบใหม่ตอนคลิกออก
        });
    }

    private static void paintInputBorder(Graphics g, JTextField field) { // ฟังก์ชันสำหรับวาดกรอบช่องกรอกข้อมูลให้มุมโค้งมน
        Graphics2D copy = (Graphics2D) g.create(); // สร้างตัววาดกราฟิก
        copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // เปิดระบบลบรอยหยัก
        copy.setColor(field.isFocusOwner() ? new Color(120, 120, 120) : new Color(213, 213, 213)); // ถ้าถูกคลิกให้ขอบสีเข้มขึ้น ถ้าไม่ได้คลิกขอบสีอ่อน
        copy.drawRoundRect(0, 0, field.getWidth() - 1, field.getHeight() - 1, 12, 12); // วาดกรอบสี่เหลี่ยมขอบมน
        copy.dispose(); // คืนหน่วยความจำ
    }

    private static void paintHint(Graphics g, JTextField field, String hint) { // ฟังก์ชันวาดตัวหนังสือคำใบ้
        if (field.getDocument().getLength() == 0) { // ถ้ายังไม่มีการพิมพ์ตัวอักษรอะไรเลย
            Graphics2D copy = (Graphics2D) g.create(); // สร้างกราฟิกตัววาด
            copy.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, // เปิดลบรอยหยักตัวอักษร
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            copy.setColor(Color.BLACK); // ให้คำใบ้สีดำ
            copy.setFont(field.getFont()); // ใช้ฟอนต์เดียวกัน
            FontMetrics metrics = copy.getFontMetrics(); // ตัววัดขนาดตัวอักษร
            copy.drawString(hint, 14, // วาดคำใบ้ลงไป เริ่มที่พิกัด X=14
                    (field.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent()); // คำนวณความสูงให้คำใบ้อยู่ตรงกลางช่องพอดี
            copy.dispose(); // คืนหน่วยความจำ
        }
    }

    static class HintTextField extends JTextField { // คลาสพิเศษ สร้างช่องกรอกข้อความที่มีคำใบ้ในตัว
        private final String hint; // เก็บคำใบ้
        HintTextField(String hint) { // Constructor รับคำใบ้เข้ามา
            this.hint = hint; // บันทึกคำใบ้
            styleInput(this, hint); // ไปเรียกใช้ฟังก์ชันตกแต่งหน้าตาช่องกรอก
        }
        @Override protected void paintComponent(Graphics g) { // แทรกแซงการวาดของเดิม
            super.paintComponent(g); // ให้วาดข้อความปกติก่อน
            paintHint(g, this, hint); // สั่งวาดคำใบ้ทับลงไป (ถ้ายาวเป็น 0)
        }
        @Override protected void paintBorder(Graphics g) { paintInputBorder(g, this); } // วาดขอบมนทับของเดิม
    }

    static class HintPasswordField extends JPasswordField { // คลาสพิเศษ สร้างช่องกรอกรหัสผ่าน (ที่ซ่อนรหัส) ที่มีคำใบ้
        private final String hint; // เก็บคำใบ้
        HintPasswordField(String hint) { // Constructor รับคำใบ้มา
            this.hint = hint; // บันทึกคำใบ้
            styleInput(this, hint); // ไปเรียกตกแต่งหน้าตา
            setEchoChar('\u2022'); // ตั้งค่าตัวอักษรปิดบังรหัสให้เป็นจุดกลมๆ •
        }
        @Override protected void paintComponent(Graphics g) { // แทรกแซงการวาด
            super.paintComponent(g); // วาดจุดไข่ปลาปกติ
            paintHint(g, this, hint); // วาดคำใบ้ถ้ายังไม่ได้พิมพ์
        }
        @Override protected void paintBorder(Graphics g) { paintInputBorder(g, this); } // วาดขอบโค้งมน
    }
}